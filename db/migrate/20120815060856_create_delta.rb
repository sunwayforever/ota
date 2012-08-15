class CreateDelta < ActiveRecord::Migration
  def change
    create_table :delta do |t|
      t.integer :a_version_id
      t.integer :b_version_id
      t.integer :size
      t.string :path
      t.integer :product_id

      t.timestamps
    end
  end
end
