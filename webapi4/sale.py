import webapp2
from google.appengine.ext import ndb
import db_defs
import datetime
import json

#referenced stackoverflow.com/questions/29184670 for issue related to making
#a datetime object JSON serializable
class MyJsonEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, datetime.datetime):
            return obj.strftime("%m/%d/%Y %H:%M")
        return json.JSONEnconder.default(self, obj)

#used code from lectures as base
class Sale(webapp2.RequestHandler):
    #Creates a Sales entity
    def post(self):
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.status_message = 'Not acceptable, API only supports application/json MIME type.'
            return
        new_sale = db_defs.Sales()
        items = self.request.get_all('items[]',default_value=None)
        dt = self.request.get('datetime',default_value=datetime.datetime.now())
        quantity = self.request.get_all('quantity[]',default_value=None)
        item_cost = self.request.get_all('item_cost[]',default_value=None)
        total_cost = self.request.get('total_cost',default_value=0)
        latitude = self.request.get('latitude', default_value=-1)
        longitude = self.request.get('longitude', default_value=-1)

        if items:
            for item in items:
                new_sale.items.append(ndb.Key(db_defs.Item, int(item)))
        if dt:
            new_sale.datetime = dt
        if quantity:
            for qty in quantity:
                new_sale.quantity.append(int(qty))
        if item_cost:
            for ic in item_cost:
                new_sale.item_cost.append(float(ic))
        for i in range(len(items)):
            try:
                total_cost += int(quantity[i]) * float(item_cost[i])
            except:
                self.response.status = 406
                self.response.status_message = "Invalid entry, each item requires quantity and item cost"
                self.response.write(self.response.status_message)
                return
        new_sale.total_cost = total_cost
        if latitude:
            new_sale.latitude = float(latitude)
        if longitude:
            new_sale.longitude = float(longitude)

        key = new_sale.put()
        out = new_sale.to_dict()
        self.response.write(json.dumps(out, cls=MyJsonEncoder))
        return

    #Returns a Sales entity
    def get(self, **kwargs):
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.status_message = 'Not acceptable, API only supports application/json MIME type.'
            self.response.write(self.response.status_message)
            return
        #returns only selected sale details
        if 'id' in kwargs:
            try:
                out = ndb.Key(db_defs.Sales, int(kwargs['id'])).get().to_dict()
            except:
                self.response.write('Sale not found.')
                return
            self.response.write(json.dumps(out, cls=MyJsonEncoder))
        #return all sales ids
        else:
            q = db_defs.Sales.query()
            keys = q.fetch(keys_only=True)
            results = {'keys' : [x.id() for x in keys]}
            self.response.write(json.dumps(results, cls=MyJsonEncoder))

    #Delete a Sales entity
    def delete(self, **kwargs):
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.status_message = 'Not acceptable, API only supports application/json MIME type.'
            self.response.write(self.response.status_message)
            return
        if 'id' in kwargs:
            sale = ndb.Key(db_defs.Sales, int(kwargs['id']))
            if not sale:
                self.response.status = 404
                self.response.status_message = "Sale not found"
                return
            sale.delete()
            self.response.write('Sale ' + kwargs['id'] + ' deleted.')


class SaleItems(webapp2.RequestHandler):
    #Update a Sale entity, add an additional item
    def put(self, **kwargs):
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.status_message = 'Not acceptable, API only supports application/json MIME type.'
            self.response.write(self.response.status_message)
            return
        if 'sid' in kwargs:
            sale = ndb.Key(db_defs.Sales, int(kwargs['sid'])).get()
            if not sale:
                self.response.status = 404
                self.response.status_message = "Sale not found"
                self.response.write(self.response.status_message)
                return
        if 'iid' in kwargs:
            item = ndb.Key(db_defs.Item, int(kwargs['iid']))
            quantity = int(kwargs['iqty'])
            cost = float(kwargs['icst'])
            #item id, quantity, and cost are all required
            if not item or not quantity or not cost:
                self.response.status = 404
                self.response.status_message = "Item id, quantity, cost required"
                self.response.write(self.response.status_message)
                return
        if item not in sale.items:
            sale.items.append(item)
            sale.quantity.append(quantity)
            sale.item_cost.append(cost)
            sale.total_cost += quantity * cost
            sale.put()
        self.response.write(json.dumps(sale.to_dict(), cls=MyJsonEncoder))
        return
