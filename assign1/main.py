import webapp2
import datetime

class MainPage(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.headers['Content-Type'] = 'text/plain'
       	self.response.write('Assignment 1 for CS 496.\n')
       	self.response.write('Jiawei Liu\n\n')
       	self.response.write('The time is: ')
       	self.response.write(datetime.datetime.now())
       	self.response.write(' \nRefresh the page to see updated time.')





app = webapp2.WSGIApplication([
    ('/', MainPage),
], debug=True)