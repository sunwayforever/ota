class Product < ActiveRecord::Base
  attr_accessible :vender
  has_many :cellulars,:dependent=>:destroy
  has_many :deltum,:dependent=>:destroy
end
