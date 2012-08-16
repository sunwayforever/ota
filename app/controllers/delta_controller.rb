class DeltaController < ApplicationController

  def push
    @deltum = Deltum.find(params[:id])
    render :text=>"deltum "+@deltum.path+" will be pused"
  end

  def get
    deltum = Deltum.find_by_id(params[:id])
    if deltum==nil then
      render :json=>{:error=>"-1"}, :status=>:failed
      return
    end
    send_file deltum.path
  end
  # GET /delta
  # GET /delta.json
  def index
    @delta = Deltum.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render :json => @delta }
    end
  end

  # GET /delta/1
  # GET /delta/1.json
  def show
    @deltum = Deltum.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render :json => @deltum }
    end
  end

  # GET /delta/new
  # GET /delta/new.json
  def new
    @deltum = Deltum.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render :json => @deltum }
    end
  end

  # GET /delta/1/edit
  def edit
    @deltum = Deltum.find(params[:id])
  end

  # POST /delta
  # POST /delta.json
  def create
    @deltum = Deltum.new(params[:deltum])

    respond_to do |format|
      if @deltum.save
        format.html { redirect_to @deltum, :notice => 'Deltum was successfully created.' }
        format.json { render :json => @deltum, :status => :created, :location => @deltum }
      else
        format.html { render :action => "new" }
        format.json { render :json => @deltum.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /delta/1
  # PUT /delta/1.json
  def update
    @deltum = Deltum.find(params[:id])

    respond_to do |format|
      if @deltum.update_attributes(params[:deltum])
        format.html { redirect_to @deltum, :notice => 'Deltum was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render :action => "edit" }
        format.json { render :json => @deltum.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /delta/1
  # DELETE /delta/1.json
  def destroy
    @deltum = Deltum.find(params[:id])
    @deltum.destroy

    respond_to do |format|
      format.html { redirect_to delta_url }
      format.json { head :no_content }
    end
  end
end
