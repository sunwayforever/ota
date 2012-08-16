class Deltum < ActiveRecord::Base
  attr_accessible :a_version_id, :b_version_id, :path, :product_id, :size
  belongs_to :a_version,:class_name=>'Version'
  belongs_to :b_version,:class_name=>'Version'
  belongs_to :product

  attr_accessible :a_version,:b_version,:product,:path,:size
  validates_presence_of :a_version,:b_version,:path,:size,:product

end
