class Deltum < ActiveRecord::Base
  include ApplicationHelper

  attr_accessible :a_version_id, :b_version_id, :path, :product_id, :size
  belongs_to :a_version,:class_name=>'Version'
  belongs_to :b_version,:class_name=>'Version'
  belongs_to :product

  attr_accessible :a_version,:b_version,:product,:path,:size,:deltum_file
  validates_presence_of :a_version,:b_version,:path,:size,:product

  before_destroy :remove_deltum

  def deltum_file=(file)
    self.size=1024
    self.path=file.original_filename
    File.open(delta_storage(self.path), "wb") do |f| 
      f.write(file.read)
    end
  end

  def remove_deltum
    File.delete delta_storage (self.path)
  end
end
