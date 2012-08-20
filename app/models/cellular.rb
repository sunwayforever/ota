class Cellular < ActiveRecord::Base
  REGISTER_FAILED=-1
  REGISTER_UPDATED=0
  REGISTERED=1
  
  attr_accessible :jid, :product_id, :version_id, :product,:version
  belongs_to :product
  belongs_to :version

  attr_accessible :vender_str,:version_str
  validates_presence_of :product,:version
  validates_uniqueness_of :jid

  def self.filter (filter_str)
    ret=Array.new
    Cellular.all.each do |c|
      all_data=c.jid+"\n"+c.version.version+"\n"+c.product.vender
      ret << c if all_data.include? filter_str
    end
    return ret
  end

  def self.register(version_str,product_str,jid)
    version=Version.find_by_version(version_str);
    product=Product.find_by_vender(product_str)
    valid= version && product
    if valid then
      cellular=Cellular.find_by_jid(jid)
      if cellular==nil then
        Cellular.create(:version=>version,:product=>product,:jid=>jid)
        return REGISTERED
      else
        cellular.version=version
        cellular.product=product
        cellular.save
        return REGISTER_UPDATED
      end
    end
    return REGISTER_FAILED
  end
end
