import os
import json
from access import data
from flask import Flask, request, jsonify


app = Flask(__name__)


@app.route('/get-framework', methods=['POST'])
def get_framework():
	uri = request.form['uri']
	return jsonify(data.get_framework(uri))


@app.route('/get-path', methods=['POST'])
def get_path():
	s = request.form['s']
	t = request.form['t']
	return jsonify(data.find_path(s, t))


@app.route('/get-recommendation', methods=['POST'])
def get_recommendation():
	course = request.form['course']
	history = json.loads(request.form['history'])

	# 默认推荐策略
	# 优先从本地读取缓存，不存在时再与数据库交互
	if os.path.exists(course+'.json'):
		with open(course+'.json', 'r') as f:
			result_default = json.load(f)
	else:
		result_default = data.get_page_rank(course, 20)
		with open(course+'.json', 'w') as f:
			f.write(json.dumps(result_default))

	# 关联推荐策略
	result_similarity = []
	if len(history) > 0:
		target = len(history) * 2  # 应该生成多长的推荐列表
		if target < 10:
			target = 10
		if target > 50:
			target = 50
		result_similarity = data.get_recommendation(history, target)

	return jsonify(result_similarity + result_default)


if __name__ == '__main__':
	app.run()
