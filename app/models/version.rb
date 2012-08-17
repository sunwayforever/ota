class Version < ActiveRecord::Base
  attr_accessible :date, :release_note, :version
  has_many :a_versions,:class_name=>'Deltum',:foreign_key=>:a_version_id,:dependent=>:destroy
  has_many :b_versions,:class_name=>'Deltum',:foreign_key=>:b_version_id,:dependent=>:destroy
  has_many :cellulars,:dependent=>:destroy

  def self.filter (filter_str)
    ret=Array.new
    Version.all.each do |v|
      all_data=v.version
      ret << v if all_data.include? filter_str
    end
    return ret
  end
end
