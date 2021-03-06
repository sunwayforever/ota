Ota::Application.routes.draw do
  match 'products/filter', :to => 'products#filter'
  resources :products

  match 'delta/filter', :to => 'delta#filter'
  match 'delta/:id/get', :to => 'delta#get'
  match 'delta/:id', :via=>'post',:to => 'delta#push'
  resources :delta

  match 'versions/filter', :to => 'versions#filter'
  resources :versions

  match 'cellulars/register', :via=>'post',:to => 'cellulars#register'
  match 'cellulars/filter', :to => 'cellulars#filter'
  match 'cellulars/query_deltum/:jid', :to => 'cellulars#query_deltum'
  match 'cellulars/:id', :via=>'post',:to => 'cellulars#push'
  resources :cellulars

  # The priority is based upon order of creation:
  # first created -> highest priority.

  # Sample of regular route:
  #   match 'products/:id' => 'catalog#view'
  # Keep in mind you can assign values other than :controller and :action

  # Sample of named route:
  #   match 'products/:id/purchase' => 'catalog#purchase', :as => :purchase
  # This route can be invoked with purchase_url(:id => product.id)

  # Sample resource route (maps HTTP verbs to controller actions automatically):
  #   resources :products

  # Sample resource route with options:
  #   resources :products do
  #     member do
  #       get 'short'
  #       post 'toggle'
  #     end
  #
  #     collection do
  #       get 'sold'
  #     end
  #   end

  # Sample resource route with sub-resources:
  #   resources :products do
  #     resources :comments, :sales
  #     resource :seller
  #   end

  # Sample resource route with more complex sub-resources
  #   resources :products do
  #     resources :comments
  #     resources :sales do
  #       get 'recent', :on => :collection
  #     end
  #   end

  # Sample resource route within a namespace:
  #   namespace :admin do
  #     # Directs /admin/products/* to Admin::ProductsController
  #     # (app/controllers/admin/products_controller.rb)
  #     resources :products
  #   end

  # You can have the root of your site routed with "root"
  # just remember to delete public/index.html.
  root :to => 'delta#index'

  # See how all your routes lay out with "rake routes"

  # This is a legacy wild controller route that's not recommended for RESTful applications.
  # Note: This route will make all actions in every controller accessible via GET requests.
  # match ':controller(/:action(/:id))(.:format)'
end
