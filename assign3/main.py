import webapp2

config = {'default-group':'base-data'}

app = webapp2.WSGIApplication([
	('/', 'admin.Admin'),
	('/admin', 'admin.Admin'),
	('/view', 'view.View'),
	('/memo/view', 'view.View'),
	('/memo/add', 'admin.AddLocation'),
	('/memo/edit', 'edit.EditLocation'),
	('/edit', 'edit.ViewEditLocation')
], debug = True, config = config)
app.router.add(webapp2.Route(r'/memos', 'memos.Memos'))
app.router.add(webapp2.Route(r'/memos/<lid:[0-9]+><:/?>', 'memos.Memos'))
app.router.add(webapp2.Route(r'/memos/<lid:[0-9]+><:/?>/comments', 'memos.MemoComments'))
