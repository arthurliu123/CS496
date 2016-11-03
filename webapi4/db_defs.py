from google.appengine.ext import ndb

#Model class, to_dict function borrowed from course example
class Model(ndb.Model):
	def to_dict(self):
		d = super(Model, self).to_dict()
		d['key'] = self.key.id()
		return d

class Item(Model):
	name = ndb.StringProperty(required=True)
	description = ndb.StringProperty(required=True)
	importance = ndb.IntegerProperty()
	finish = ndb.StringProperty()

class Sales(Model):
	items = ndb.KeyProperty(repeated=True)
	datetime = ndb.DateTimeProperty(required=True)
	quantity = ndb.IntegerProperty(repeated=True)
	item_cost = ndb.FloatProperty(repeated=True)
	total_cost = ndb.FloatProperty()
	latitude = ndb.FloatProperty()
	longitude = ndb.FloatProperty()
	


	def to_dict(self):
		d = super(Sales, self).to_dict()
		d['items'] = [i.id() for i in d['items']]
		return d 
