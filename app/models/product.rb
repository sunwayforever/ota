class Product < ActiveRecord::Base
  attr_accessible :vender
  has_many :cellulars
  has_many :deltum
end
