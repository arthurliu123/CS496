import webapp2
from google.appengine.ext import ndb
import db_defs
import json

class Item(webapp2.RequestHandler):
    #Create an Item entity
    def post(self):
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.status_message = 'Not acceptable, API only supports application/json MIME type.'
            return
        new_item = db_defs.Item()
        name = self.request.get('name', default_value=None)
        description = self.request.get('description', default_value=None)
        importance = self.request.get('importance', default_value=0)
        finish = self.request.get('finish', default_value=None)
        

        if name:
            new_item.name = name
        else:
            self.response.status = 400
            self.response.status_message = 'Invalid request, name required'
        if description:
            new_item.description = description
        else:
            self.response.status = 400
            self.response.status_message = 'Invalid request, description required'
        if importance:
            new_portanceitem.importance = int(importance)
        if finish:
            new_item.finish = finish

        key = new_item.put()
        out = new_item.to_dict()
        self.response.write(json.dumps(out))
        return

    #Return an Item entity
    def get(self, **kwargs):
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.status_message = 'Not acceptable, API only supports application/json MIME type.'
            self.response.write(self.response.status_message)
            return
        #Return selected item details
        if 'id' in kwargs:
            out = ndb.Key(db_defs.Item, int(kwargs['id'])).get().to_dict()
            self.response.write(json.dumps(out))
        #Return all item ids
        else:
            q = db_defs.Item.query()
            keys = q.fetch(keys_only=False)
            #results = {'keys' : [x.id() for x in keys]}
            results = {x.key.id() : x.to_dict() for x in keys}
            self.response.write(json.dumps(results))
