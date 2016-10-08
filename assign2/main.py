import webapp2

config = {'default-group':'base-data'}

app = webapp2.WSGIApplication([
	('/', 'admin.Admin'),
	('/admin', 'admin.Admin'),
	('/view', 'view.View'),
	('/location/view', 'view.View'),
	('/location/add', 'admin.AddLocation'),
	('/location/edit', 'edit.EditLocation'),
	('/edit', 'edit.ViewEditLocation')
], debug = True, config = config)
app.router.add(webapp2.Route(r'/locations', 'locations.Locations'))
app.router.add(webapp2.Route(r'/locations/<lid:[0-9]+><:/?>', 'locations.Locations'))
app.router.add(webapp2.Route(r'/locations/<lid:[0-9]+><:/?>/comments', 'locations.LocationComments'))
