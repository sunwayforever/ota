class Cellular < ActiveRecord::Base
  attr_accessible :jid, :product_id, :version_id
  belongs_to :product
  belongs_to :version
end
