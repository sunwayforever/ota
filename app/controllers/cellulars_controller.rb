class CellularsController < ApplicationController
  # GET /cellulars
  # GET /cellulars.json
  def index
    @cellulars = Cellular.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render :json => @cellulars }
    end
  end

  # GET /cellulars/1
  # GET /cellulars/1.json
  def show
    @cellular = Cellular.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render :json => @cellular }
    end
  end

  # GET /cellulars/new
  # GET /cellulars/new.json
  def new
    @cellular = Cellular.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render :json => @cellular }
    end
  end

  # GET /cellulars/1/edit
  def edit
    @cellular = Cellular.find(params[:id])
  end

  # POST /cellulars
  # POST /cellulars.json
  def create
    @cellular = Cellular.new(params[:cellular])

    respond_to do |format|
      if @cellular.save
        format.html { redirect_to @cellular, :notice => 'Cellular was successfully created.' }
        format.json { render :json => @cellular, :status => :created, :location => @cellular }
      else
        format.html { render :action => "new" }
        format.json { render :json => @cellular.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /cellulars/1
  # PUT /cellulars/1.json
  def update
    @cellular = Cellular.find(params[:id])

    respond_to do |format|
      if @cellular.update_attributes(params[:cellular])
        format.html { redirect_to @cellular, :notice => 'Cellular was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render :action => "edit" }
        format.json { render :json => @cellular.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /cellulars/1
  # DELETE /cellulars/1.json
  def destroy
    @cellular = Cellular.find(params[:id])
    @cellular.destroy

    respond_to do |format|
      format.html { redirect_to cellulars_url }
      format.json { head :no_content }
    end
  end
end
