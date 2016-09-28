# Copyright 2016 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import webapp2
import datetime

class MainPage(webapp2.RequestHandler):
    def get(self):
        self.response.write('<h3>Welcome to my webpage for CS 496</h3>')
        self.response.write('<h4>### Assignment 1 ###</h4>')
        self.response.write('<h4>Author: Jiawei Liu</h4>')
        self.response.write('Current server(UTC/GMT) time is: ')
        self.response.write(datetime.datetime.now())
        self.response.write('<br><br> <img src="http://img.memecdn.com/doge-time_o_2448531.webp">')

app = webapp2.WSGIApplication([
    ('/', MainPage),
], debug=True)