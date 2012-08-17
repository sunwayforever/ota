class Cellular < ActiveRecord::Base
  attr_accessible :jid, :product_id, :version_id, :product,:version
  belongs_to :product
  belongs_to :version

  attr_accessible :vender_str,:version_str
  validates_presence_of :product,:version

  def self.filter (filter_str)
    ret=Array.new
    Cellular.all.each do |c|
      all_data=c.jid+"\n"+c.version.version+"\n"+c.product.vender
      ret << c if all_data.include? filter_str
    end
    return ret
  end
end
