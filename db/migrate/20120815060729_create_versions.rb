class CreateVersions < ActiveRecord::Migration
  def change
    create_table :versions do |t|
      t.date :date
      t.string :version
      t.string :release_note

      t.timestamps
    end
  end
end
