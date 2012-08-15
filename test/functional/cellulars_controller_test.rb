require 'test_helper'

class CellularsControllerTest < ActionController::TestCase
  setup do
    @cellular = cellulars(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:cellulars)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create cellular" do
    assert_difference('Cellular.count') do
      post :create, :cellular => { :jid => @cellular.jid, :model_id => @cellular.model_id, :version_id => @cellular.version_id }
    end

    assert_redirected_to cellular_path(assigns(:cellular))
  end

  test "should show cellular" do
    get :show, :id => @cellular
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => @cellular
    assert_response :success
  end

  test "should update cellular" do
    put :update, :id => @cellular, :cellular => { :jid => @cellular.jid, :model_id => @cellular.model_id, :version_id => @cellular.version_id }
    assert_redirected_to cellular_path(assigns(:cellular))
  end

  test "should destroy cellular" do
    assert_difference('Cellular.count', -1) do
      delete :destroy, :id => @cellular
    end

    assert_redirected_to cellulars_path
  end
end
