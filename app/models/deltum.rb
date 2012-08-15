class Deltum < ActiveRecord::Base
  attr_accessible :a_version_id, :b_version_id, :path, :product_id, :size
  belongs_to :a_version,:class_name=>'Version'
  belongs_to :b_version,:class_name=>'Version'
  belongs_to :product
end
