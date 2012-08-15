class Deltum < ActiveRecord::Base
  attr_accessible :a_version_id, :b_version_id, :path, :product_id, :size
  belongs_to :a_version,:class_name=>'Version'
  belongs_to :b_version,:class_name=>'Version'
  belongs_to :product

  attr_accessor :a_version_str,:b_version_str,:vender_str
  attr_accessible :a_version_str,:b_version_str,:vender_str,:path,:size
  validates_presence_of :a_version,:b_version,:path,:size,:product

  def vender_str=(vender_str)
    self.product=Product.find_by_vender(vender_str)
  end

  def a_version_str=(version_str)
    self.a_version=Version.find_by_version(version_str)
  end

  def b_version_str=(version_str)
    self.b_version=Version.find_by_version(version_str)
  end
end
