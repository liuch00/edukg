from py2neo import Graph
from py2neo.data import Path


graph = Graph('http://123.60.95.95:7474', auth=('neo4j', 'g_rD%_6w@bFA8dL'))


def get_label_for_single_entity(uri):
	'''
	获得一个实体的标签属性
	:return: [label (string)] 若没有查询结果则会返回空列表
	'''
	code = 'match (n:Entity {uri: "%s"})-[r {uri: "http://www.w3.org/2000/01/rdf-schema#label"}]->(p:Property)\n' \
		'return p.content' % uri

	result = graph.run(code)

	return_val = []
	for entry in result:
		return_val.append(entry['p.content'])
	return return_val


def get_all_properties(uri):
	'''
	获得一个实体的所有属性
	:return: [ [property_type, property_content], ... ] 若没有查询结果则会返回空列表
	'''
	code = 'match (n:Entity {uri: "%s"})-[r]->(p:Property)\n' \
		'match (rn:Entity {uri: r.uri})-[{uri: "http://www.w3.org/2000/01/rdf-schema#label"}]->(rp:Property)\n' \
		'return rp, p' % uri

	result = graph.run(code)

	return_val = []
	for entry in result:
		return_val.append([entry['rp']['content'], entry['p']['content']])

	return return_val


def get_framework(uri):
	'''
	获得关联实体大纲
	:return:
		{
			'uri': '',
			'label': '',
			'rel': '', # 与上级实体关联的关系，以标签形式给出
			'sub': []
		}
		sub 中的每一项都递归采用相同的结构
		若没有查询结果则返回空字典
	'''
	def get_label_or_empty(uri):
		result = get_label_for_single_entity(uri)
		if len(result) > 0:
			return result[0]
		return ''

	def get_label_or_uri(uri):
		result = get_label_for_single_entity(uri)
		if len(result) > 0:
			return result[0]
		return uri

	def insert_record_into_dict(target, uri, rel=None, at_end=False):
		'''
		向指定词典中加入指定记录
		'''
		if uri not in target:
			label = get_label_or_empty(uri)
			if rel:
				rel = get_label_or_uri(rel)
			else:
				rel = ''
			if not at_end:
				target[uri] = {'uri': uri, 'label': label, 'rel': rel, 'sub': {}}
			else:
				target[uri] = {'uri': uri, 'label': label, 'rel': rel}

	# 检查是否是合法输入
	code = 'match (n {uri: "%s"}) return n' % uri
	result = list(graph.run(code))
	if not len(result) > 0:
		return {}

	# 将起点写入词典
	internal_val = {}
	insert_record_into_dict(internal_val, uri)

	# 获得内圈
	code = 'match (s {uri: "%s"})-[r1]-(t1:Entity)\n' \
		'with s, r1, t1 limit 5\n' \
		'return r1.uri as r1, t1.uri as t1, s.course as course\n' % uri
	result = graph.run(code)

	for entry in result:
		course = entry['course']
		insert_record_into_dict(
			internal_val[uri]['sub'],
			entry['t1'],
			entry['r1']
		)

	# 获得外圈
	source_list = '['
	for entry in internal_val[uri]['sub'].keys():
		source_list += '"'  + entry + '"' + ', '
	source_list = source_list[:-2] + ']'
	code = 'with ' + source_list + ' as source\n' \
		'match (t1)-[r2]-(t2:Entity)\n' \
		'where (t2.course = "%s") and (t1.uri in source) and (not t2.uri in source) and (t2.uri <> "%s")\n' \
		'with t1, r2, t2 limit 15\n' \
		'return t1.uri as t1, r2.uri as r2, t2.uri as t2' % (course, uri)
	result = graph.run(code)

	t2_to_avoid_loop = {}
	for entry in result:
		if entry['t2'] in t2_to_avoid_loop:
			continue
		t2_to_avoid_loop[entry['t2']] = True
		insert_record_into_dict(
			internal_val[uri]['sub'][entry['t1']]['sub'],
			entry['t2'],
			entry['r2'],
			True
		)

	# 将 sub 转成列表
	for mid_value in internal_val[uri]['sub'].values():
		mid_value['sub'] = list(mid_value['sub'].values())
		if not len(mid_value['sub']) > 0:
			del mid_value['sub']
	internal_val[uri]['sub'] = list(internal_val[uri]['sub'].values())
	if not len(internal_val[uri]['sub']) > 0:
		del internal_val[uri]['sub']

	return internal_val[uri]


def print_framework(uri):
	'''
	通过结构化打印的方式展示 framework 返回结果的层次结构
	'''
	result = get_framework(uri)
	print(result['label'])
	print(result['uri'])
	if 'sub' not in result:
		return
	for mid in result['sub']:
		print('\t', mid['rel'], '---')
		print('\t', mid['label'])
		print('\t', mid['uri'])
		if 'sub' not in mid:
			continue
		for end in mid['sub']:
			print('\t\t', end['rel'], '---')
			print('\t\t', end['label'])
			print('\t\t', end['uri'])


def find_path(s, t):
	'''
	找到最短路
	:return:
		[
			[
				{
					'type': '',
					'uri': '',
					'label': ''
				},
				...
			],  # 这是一条路径
			...
		]
		若没有查询结果则返回空列表
	'''
	code = 'match (p1:Entity {uri: "%s"}), (p2:Entity {uri: "%s"}),\n' \
		'path=allshortestpaths((p1)-[*..10]-(p2))\n' \
		'with path limit 5\n' \
		'return path' % (s, t)

	result = graph.run(code)

	def get_relation(n1, n2):
		code = 'match ({uri: "%s"})-[r]-({uri: "%s"}) return r' % (n1, n2)
		result = graph.run(code)
		return_val = []
		for entry in result:
			return_val.append(entry['r']['uri'])
		return return_val[0]

	def get_label_or_empty(uri):
		result = get_label_for_single_entity(uri)
		if len(result) > 0:
			return result[0]
		return ''

	return_val = []
	for entry in result:
		path_list = []
		pred_entity = None
		pred_rel = None
		for node in entry['path'].nodes:
			if pred_entity:  # 从第二个实体开始，搜索前置的关系
				pred_rel = get_relation(pred_entity, node['uri'])

			if pred_rel:  # 从第二个实体开始，添加前置的关系
				path_list.append({
					'type': 'Relation',
					'uri': pred_rel,
					'label': get_label_or_empty(pred_rel)
				})

			# 添加实体本身
			path_list.append({
				'type': 'Entity',
				'uri': node['uri'],
				'label': get_label_or_empty(node['uri'])
			})
			pred_entity = node['uri']

		return_val.append(path_list)

	return return_val


def get_node_by_id(nodeId):
	code = 'match (n)-[r {uri: "http://www.w3.org/2000/01/rdf-schema#label"}]->(p:Property)\n' \
		'where id(n) = %d\n' \
		'return n.uri as uri, p.content as label' % nodeId
	result = list(graph.run(code))
	if len(result) > 0:
		return result[0]
	return None


def get_page_rank(course, top_x=10):
	'''
	根据 pageRank 算法，找到最重要的 top_x 个实体
	:return:
		[
			{
				'uri': '',
				'label': ''
			},
			...
		]
	(约定如果一个实体没有 label，就将其忽略)
	'''
	code = 'call gds.pageRank.stream("%s")\n' \
		'yield score, nodeId' % course

	result = graph.run(code)

	result = list(result)  # 根据得分降序排列
	result.sort(key=(lambda x: x['score']), reverse=True)

	return_val = []
	r = 0
	while len(return_val) < top_x and r < len(result):
		node = get_node_by_id(result[r]['nodeId'])
		if node:
			return_val.append({'uri': node['uri'], 'label': node['label']})
		r += 1

	return return_val


def get_recommendation(history, top_x=10):
	'''
	根据 Jaccard 指数进行推荐，至多找到关联性最强的 top_x 个实体
	:return:
		[
			{
				'uri': '',
				'label': ''
			},
			...
		]
	(约定如果一个实体没有 label，就将其忽略)
	'''
	history_str = '['
	for entry in history:
		history_str += '"'  + entry + '"' + ', '
	history_str = history_str[:-2] + ']'

	code = 'with ' + history_str + ' as source\n\n' \
		'match (s)-[]-(s1:Entity) where s.uri in source\n' \
		'with source, s, collect(id(s))+collect(id(s1)) as sSet\n\n' \
		'match (t:Entity)-[]-(t1:Entity) where (t.course = s.course) and (not t.uri in source)\n' \
		'with sSet, t, collect(id(t))+collect(id(t1)) as tSet\n\n' \
		'with t, gds.alpha.similarity.jaccard(sSet, tSet) as similarity\n' \
		'return id(t) as t order by similarity desc'

	result = list(graph.run(code))

	return_val = []
	r = 0
	while len(return_val) < top_x and r < len(result):
		node = get_node_by_id(result[r]['t'])
		if node:
			return_val.append({'uri': node['uri'], 'label': node['label']})
		r += 1

	return return_val


test_uri = [
	'http://edukb.org/knowledge/0.1/instance/chinese#-115d0537a174a91dfd9cafcea783be83',
	'http://edukb.org/knowledge/0.1/instance/chinese#-2c99e0a826dd5f8e3342684fcdc874d1',
	'http://edukg.org/knowledge/0.1/instance/english#calm-6c2729612e1dc1ef2537637b9c24bc9c',
]

#print_framework(test_uri[1])
