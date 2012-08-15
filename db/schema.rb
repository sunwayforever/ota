# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20120815060947) do

  create_table "cellulars", :force => true do |t|
    t.integer  "product_id"
    t.integer  "version_id"
    t.string   "jid"
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  create_table "delta", :force => true do |t|
    t.integer  "a_version_id"
    t.integer  "b_version_id"
    t.integer  "size"
    t.string   "path"
    t.integer  "product_id"
    t.datetime "created_at",   :null => false
    t.datetime "updated_at",   :null => false
  end

  create_table "products", :force => true do |t|
    t.string   "vender"
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  create_table "versions", :force => true do |t|
    t.date     "date"
    t.string   "version"
    t.string   "release_note"
    t.datetime "created_at",   :null => false
    t.datetime "updated_at",   :null => false
  end

end
