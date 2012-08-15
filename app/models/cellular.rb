class Cellular < ActiveRecord::Base
  attr_accessible :jid, :product_id, :version_id
  belongs_to :product
  belongs_to :version

  attr_accessible :vender_str,:version_str
  validates_presence_of :product,:version

  def vender_str
    return self.product.vender if self.product!=nil
  end
  def version_str
    return self.version.version if self.product!=nil
  end
  def vender_str=(vender_str)
    self.product=Product.find_by_vender(vender_str)
  end
  def version_str=(version_str)
    self.version=Version.find_by_version(version_str)
  end
end