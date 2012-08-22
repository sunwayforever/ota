require 'rubygems'
require 'xmpp4r'
module DeltaHelper
  def xmpp_handle_push (deltum)
    xmpp_client=Jabber::Client.new(Jabber::JID::new("ota_pusher@localhost/ota"))
    xmpp_client.connect("127.0.0.1",5222)
    xmpp_client.auth("ota_pusher")

    data=deltum.to_json (:include=>[:b_version,:a_version,:product])
    cellulars=Cellular.where :product_id=>deltum.product,:version_id=>deltum.a_version
    cellulars.each do |c|
      message=Jabber::Message.new(c.jid+"@localhost/ota",data)
      xmpp_client.send message
    end
    xmpp_client.close
    return cellulars.size
  end
end
