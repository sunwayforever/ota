require 'test_helper'

class DeltaControllerTest < ActionController::TestCase
  setup do
    @deltum = delta(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:delta)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create deltum" do
    assert_difference('Deltum.count') do
      post :create, :deltum => { :a_version_id => @deltum.a_version_id, :b_version_id => @deltum.b_version_id, :path => @deltum.path, :product_id => @deltum.product_id, :size => @deltum.size }
    end

    assert_redirected_to deltum_path(assigns(:deltum))
  end

  test "should show deltum" do
    get :show, :id => @deltum
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => @deltum
    assert_response :success
  end

  test "should update deltum" do
    put :update, :id => @deltum, :deltum => { :a_version_id => @deltum.a_version_id, :b_version_id => @deltum.b_version_id, :path => @deltum.path, :product_id => @deltum.product_id, :size => @deltum.size }
    assert_redirected_to deltum_path(assigns(:deltum))
  end

  test "should destroy deltum" do
    assert_difference('Deltum.count', -1) do
      delete :destroy, :id => @deltum
    end

    assert_redirected_to delta_path
  end
end
