class Version < ActiveRecord::Base
  attr_accessible :date, :release_note, :version
  has_many :a_versions,:class_name=>'Deltum',:foreign_key=>:a_version_id
  has_many :b_versions,:class_name=>'Deltum',:foreign_key=>:b_version_id
  has_many :cellulars
end