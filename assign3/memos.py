from libraries import *
from basePage import *
import ndb_definitions
import json
from google.appengine.ext import ndb

class Memos(BaseHandler):
	def get(self, **args):
		if 'application/json' not in self.request.accept:
			self.response.status = 400
			self.respone.status_message = 'ONLY application/json is supported.'
			return
		if 'lid' in args:
			loc = ndb.Key(ndb_definitions.MM, int(args['lid'])).get().to_dict()
			self.response.write(json.dumps(loc))
			self.response.write('\n')
		else:
			query = ndb_definitions.MM.query()
			locations = query.fetch()
			results = []
			for l in locations:
				results.append(l.to_dict())
			self.response.write(json.dumps(results))
			self.response.write('\n')

	def post(self):
		if 'application/json' not in self.request.accept:
			self.response.status = 400
			self.response.status_message = 'ONLY application/json is supported.'
			self.response.write('ONLY application/json is supported.\n')
			return

		else:
			new_loc = ndb_definitions.MM()
			title = self.request.get('title', default_value=None)
			description = self.request.get('description', default_value=None)
			image = self.request.get('image', default_value=None)
			improtance = self.request.get('improtance', default_value=None)
			comments = []
			jsonstring = self.request.body
			MM_M = None
			try:
				MM_M = json.loads(jsonstring)
			except:
				pass

			if MM_M is not None:
				if 'title' not in MM_M:
					self.response.status = 400
					self.response.status_message = 'Invalid request - a memo title is required for [POST].'
					self.response.write('Invalid request - a memo title is required.\n')
					return
				else:
					title = MM_M['title']
				if 'description' in MM_M:
					description = MM_M['description']
				if 'image' in MM_M:
					image = MM_M['image']
				if 'improtance' in MM_M:
					improtance = int(MM_M['improtance'])
				if 'comments' in MM_M:
					for all_comments, comment in MM_M['comments'].iteritems():
						key = ndb.Key(Comment, self.app.config.get('default-group'))
						a_comment = Comment(parent = key)
						a_comment.author = comment['author']
						a_comment.body = comment['body']
						comments.append(a_comment)
			if title:
				new_loc.title = title
			else:
				self.response.status = 400
				self.response.status_message = 'Invalid request - a memo title is required [POST].'
				self.response.write('Invalid request - a memo title is required for a [POST].\n')
				return

			if description:
				new_loc.description = description
			if image:
				new_loc.image = image
			if improtance:
				new_loc.improtance = improtance
			if comments:
				new_loc.comments = comments
			key = new_loc.put()
			self.response.status = 200
			self.response.status_message = 'Successful write to datastore.'
			self.response.write('Successful write to datastore.')
			self.response.write('\n')
			return

	def delete(self, **args):
		if 'application/json' not in self.request.accept:
			self.response.status = 400
			self.response.status_message = 'ONLY application/json is supported.'
			self.response.write('ONLY application/json is supported.\n')
			return

		else:
			if 'lid' in args:
				loc = ndb_definitions.MM.query(ancestor=ndb.Key(MM, int(args['lid'])))
				ndb.delete_multi(loc.fetch(keys_only=True))

				message = 'Memo ID: ' + str(args['lid']) + ' has been deleted from datastore.\n'
				self.response.status = 200
				self.response.status_message = message
				self.response.write(message)

			else:
				message = 'Invalid request. A Memo ID must be specified to delete a Memo from datastore.\n'
				self.response.status = 400
				self.response.status_message = message
				self.response.write(message)

class MemoComments(BaseHandler):
	def get(self, **args):
		if 'application/json' not in self.request.accept:
			self.response.status = 400
			self.respone.status_message = 'ONLY application/json is supported.'
			return
		else:
			if 'lid' in args:
				loc = ndb.Key(ndb_definitions.MM, int(args['lid'])).get().to_dict()
				comment = loc['comments']
				self.response.write(json.dumps(comment))
				self.response.write('\n')
				self.response.write(str(len(comment)))
				self.response.write('\n')
			else:
				message = 'Invalid request. An Memo ID must be specified to retrieve comments for a Memo.'
				self.response.status = 400
				self.response.status_message = message
				self.response.write(message)

	def put(self, **args):
		if 'application/json' not in self.request.accept:
			self.response.status = 400
			self.respone.status_message = 'ONLY application/json is supported.'
			return
		else:
			jsonstring = self.request.body
			MM_M = json.loads(jsonstring)
			loc = ndb.Key(ndb_definitions.MM, int(args['lid'])).get()
			if 'comments' in MM_M:
				for all_comments, comment in MM_M['comments'].iteritems():
					loc.comments.append(comment)
			if 'title' in MM_M:
				loc.title = MM_M['title']
			if 'description' in MM_M:
				loc.description = MM_M['description']
			if 'improtance' in MM_M:
				loc.improtance = MM_M['improtance']
			if 'image' in MM_M:
				loc.image = MM_M['image']
			if 'finish' in MM_M:
				loc.finish = locatin_obj['finish']
			
			loc.put()
			self.response.status = 200
			message = 'Successful update of data store memo ID: ' + str(args['lid']) + '\n'
			self.response.status_message = message
			self.response.write(message)