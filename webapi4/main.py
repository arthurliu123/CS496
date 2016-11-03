import webapp2
from google.appengine.api import oauth

# Arbitary name of 'base-data'
config = {'default-group':'base-data'}

application = webapp2.WSGIApplication([
	('/memos', 'item.Item'),
	('/sale', 'sale.Sale'),
], debug=True, config=config)
application.router.add(webapp2.Route(r'/item/<id:[0-9]+><:/?>','item.Item'))
application.router.add(webapp2.Route(r'/item/search','item.ItemSearch')) 
application.router.add(webapp2.Route(r'/sale/<id:[0-9]+><:/?>','sale.Sale'))
application.router.add(webapp2.Route(r'/sale/<sid:[0-9]+>/item/<iid:[0-9]+>/<iqty:[0-9]+>/<icst:[0-9]+.[0-9]{1,2}><:/?>','sale.SaleItems'))
