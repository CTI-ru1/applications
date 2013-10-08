import urllib2

URL      = 'https://services.intelen.com/partners_api/index.php/energy_data/meters/format/json/interval/day'
Username = 'gen6'
Password = 'a4aef259c723c6ccd777d11a9c94a974f18eb'

authhandler = urllib2.HTTPDigestAuthHandler()
authhandler.add_password("REST API", URL, Username, Password)
opener = urllib2.build_opener(authhandler)
urllib2.install_opener(opener)
page_content = urllib2.urlopen(URL).read()
print page_content
