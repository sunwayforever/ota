require 'rubygems'
require 'xmpp4r'
module CellularsHelper
  def xmpp_handle_register (jid)
    xmpp_client=Jabber::Client.new(Jabber::JID::new(jid+"@localhost/ota"))
    xmpp_client.connect("127.0.0.1",5222)
    xmpp_client.register(jid)
    xmpp_client.close
  end

  def xmpp_handle_deregister(jid)
      xmpp_client=Jabber::Client.new(Jabber::JID::new(jid+"@localhost/ota"))
      xmpp_client.connect("127.0.0.1",5222)
      xmpp_client.auth(jid)
      xmpp_client.remove_registration();
      xmpp_client.close
  end

  def xmpp_handle_push (jid)
    xmpp_client=Jabber::Client.new(Jabber::JID::new("ota_pusher@localhost/ota"))
    xmpp_client.connect("127.0.0.1",5222)
    xmpp_client.auth("ota_pusher")

    message=Jabber::Message.new(jid+"@localhost/ota","push")
    xmpp_client.send message
    xmpp_client.close
  end

end
