import json
import requests

username = 'testuser'
password = 'testpw'

base_url = 'http://edukgappserver:8080'
headers = {}
proxies = { "http": None, "https": None} 
 
# get
def get(url, params):
	'''
	:param url: string，除 edukgapp 域名外的部分
	:param params: dict
	'''
	url = base_url + url
	connection = '?'
	for key, value in params.items():
		url += connection + key + '=' + value
		connection = '&'
	return requests.get(url, headers=headers,proxies=proxies).json()

# post
def post(url, params):
	'''
	:param url: string，除 edukgapp 域名外的部分
	:param params: dict
	'''
	url = base_url + url
	return requests.post(url, json.dumps(params), headers=headers).json()


# 初始化 token
headers = {
	'Content-Type': 'application/json',
	'Authorization': requests.get(base_url+'/auth/token?username='+username+'&password='+password,proxies=proxies).text
}


# 具体干活

result = get('/service/entity-detail', {'course': 'chinese', 'name': '李白'})
print(json.dumps(result, indent=2))

result = post('/service/instance-linkage', {'course': 'chinese', 'text': '李白是唐朝诗人，代表作《将进酒》'})
print(json.dumps(result, indent=2))
