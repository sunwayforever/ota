class Product < ActiveRecord::Base
  attr_accessible :vender
  has_many :cellulars,:dependent=>:destroy
  has_many :deltum,:dependent=>:destroy

  def self.filter (filter_str)
    ret=Array.new
    Product.all.each do |p|
      all_data=p.vender
      ret << p if all_data.include? filter_str
    end
    return ret
  end
end
