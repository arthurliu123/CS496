from google.appengine.ext import ndb

class Model(ndb.Model):
	def to_dict(self):
		d = super(Model, self).to_dict()
		d['key'] = self.key.id()
		return d

class Comment(ndb.Model):
	author = ndb.StringProperty(required=True)
	body = ndb.StringProperty()
	timestamp = ndb.DateTimeProperty(auto_now_add=True)

class Location(ndb.Model):
	title = ndb.StringProperty(required=True)
	description = ndb.StringProperty()
	finish = ndb.BooleanProperty()
	image = ndb.BlobProperty()
	improtance = ndb.IntegerProperty()
	comments = ndb.StructuredProperty(Comment, repeated=True)

	def to_dict(self):
		all_comments = {}
		a = 1
		for c in self.comments:
			all_comments['comment' + str(a)] = {"author": c.author, "body": c.body, "timestamp": str(c.timestamp)}
			a += 1

		return {
			'id': self.key.id(),
			'title': self.title,
			'description': self.description,
			'finish': self.finish,
			'image': self.image,
			'improtance': self.improtance,
			'comments': all_comments
		}

class User(ndb.Model):
	title = ndb.StringProperty(required=True)
	email = ndb.StringProperty(required=True)
	image = ndb.BlobProperty()
	U_title = ndb.StringProperty(repeated=True)
	improtance = ndb.IntegerProperty()