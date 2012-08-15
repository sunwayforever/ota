class CreateCellulars < ActiveRecord::Migration
  def change
    create_table :cellulars do |t|
      t.integer :product_id
      t.integer :version_id
      t.string :jid

      t.timestamps
    end
  end
end
