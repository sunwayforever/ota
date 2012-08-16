class Cellular < ActiveRecord::Base
  attr_accessible :jid, :product_id, :version_id, :product,:version
  belongs_to :product
  belongs_to :version

  attr_accessible :vender_str,:version_str
  validates_presence_of :product,:version

end
