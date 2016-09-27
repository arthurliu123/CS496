import webapp2


class MainPage(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.write('<script>
		var d = new Date();
		document.getElementById("demo").innerHTML = d.toString();
		</script>')





app = webapp2.WSGIApplication([
    ('/', MainPage),
], debug=True)