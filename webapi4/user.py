import webapp2
from google.appengine.ext import ndb
import db_defs
import json
import base64

class User(webapp2.RequestHandler):
    #Create a User entity
    def post(self):
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.status_message = 'Not acceptable, API only supports application/json MIME type.'
            return
        new_user = db_defs.User()
        username = self.request.get('username', default_value=None)
        password = self.request.get('password', default_value=None)

        if username:
            new_user.username = username
        else:
            self.response.status = 400
            self.response.status_message = 'Invalid request, username required'
        if password:
            new_user.password = base64.urlsafe_b64encode(password)
        else:
            self.response.status = 400
            self.response.status_message = 'Invalid request, password required'
        key = new_user.put()
        out = new_user.password #to_dict()
        #return the encrypted password for verification
        self.response.write(out)
        return

    #Return a User entity
    def get(self, **kwargs):
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.status_message = 'Not acceptable, API only supports application/json MIME type.'
            self.response.write(self.response.status_message)
            return
        #Return selected item details
        if 'username' in kwargs:
            try:
                q = db_defs.User.query().filter(ndb.GenericProperty('username') == kwargs['username']).get()
                out = q.password
                self.response.write(json.dumps(out))
            except:
                self.response.status = 406
                self.response.status_message = 'User not found.'
                self.response.write(self.response.status_message)
        #Return all item ids
        else:
            self.response.status = 406
            self.response.status_message = 'Please search for a user by username only.'
            self.response.write(self.response.status_message)
