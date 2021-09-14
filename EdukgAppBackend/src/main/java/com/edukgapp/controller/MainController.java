package com.edukgapp.controller;

import com.edukgapp.controller.entity.*;
import com.edukgapp.database.*;
import com.edukgapp.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

// 主控制器，处理导向edukg和用户数据库的请求
@RestController
@RequestMapping("/service")
public class MainController {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	// edukg 凭证相关
	private String edukgToken; // 访问 edukg 的身份凭证
	private Date edukgExpirationTime = new Date(0L); // 上述 token 的过期时间
	private final long TOKEN_VALIDITY_TIME = 60 * 60; // token 有效时间

	// 与数据库交互所用的接口
	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private FavoriteRepository favoriteRepository;

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private AppInfoRepository appInfoRepository;

	// 学科分类列表
	private HashMap<String, Object> entityListByCourse = null;

	// --- 各项主要接口 --- //
	// 实体搜索
	@GetMapping("/entity-list-by-search")
	public ResponseEntity<?> searchEntity(
			@RequestParam(value = "course", defaultValue = "chinese") String course,
			@RequestParam(value = "key", defaultValue = "李白") String key
	) {
		String target = "http://open.edukg.cn/opedukg/api/typeOpen/open/instanceList";
		String param = "?course={1}&searchKey={2}&id={3}";

		if (!isCourseValid(course))
			return ResponseEntity.badRequest().body("Invalid course code");

		EntitySearchBody response = restTemplate.getForObject(
				target+param, EntitySearchBody.class,
				course, key, getEdukgToken()
		);

		return ResponseEntity.ok(response.getData());
	}

	// 学科分类列表
	@GetMapping("/entity-list-by-course")
	public ResponseEntity<?> courseList(
			@RequestParam(value = "course", defaultValue = "chinese") String course
	) {
		if (!isCourseValid(course))
			return ResponseEntity.badRequest().body("Invalid course code");

		if (entityListByCourse == null) {
			entityListByCourse = new HashMap<>();
			// 语文
			entityListByCourse.put("chinese", new String[]{
					"语言风格", "对联", "古代诗歌", "文章", "文学体裁", "字形", "句式", "句子", "其他",
					"作家", "熟语", "文学作品", "词语", "标点符号", "人物", "字音", "汉字", "注释",
					"病句类型", "词类活用", "表达技巧", "古今异义", "文学常识", "文学形象", "现代文阅读与赏析",
					"语文方法", "文化常识", "朝代"
			});
			// 数学
			entityListByCourse.put("math", new String[]{
					"数学关系", "数学运算", "数学命题", "数学概念", "数学问题", "数学方法", "数学定义",
					"数学单位", "数学家"
			});
			// 英语
			entityListByCourse.put("english", new String[]{
					"英语语法", "英语表达", "英语语音", "英语篇章"
			});
			// 物理
			entityListByCourse.put("physics", new String[]{
					"物理概念", "物理现象", "物理定律", "物理人物", "物理仪器", "物理实验", "物理量",
					"物理方法", "物理单位", "物理应用"
			});
			// 化学
			entityListByCourse.put("chemistry", new String[]{
					"化学物质", "化学实验", "化学人物", "化学方法", "化学工业", "化学概念", "化学材料",
					"化学仪器", "化学原理"
			});
			// 生物
			entityListByCourse.put("biology", new String[]{
					"实验试剂", "生物方法", "实验仪器", "自养生物", "生物实验", "生物名题", "生物定律",
					"生物模型", "生物人物", "生物关系", "生物时期", "生物现象", "生物概念", "异养生物",
					"生物学说", "生物技术", "细胞生物", "非细胞生物"
			});
			// 历史
			entityListByCourse.put("history", new String[]{
					"思想理论", "历史分析方法", "历史事件", "历史人物", "历史观点", "历史概念", "历史时期"
			});
			// 地理
			entityListByCourse.put("geo", new String[]{
					"地理方法", "地理原理", "地理人物", "地理事实", "地理概念"
			});
			// 政治
			entityListByCourse.put("politics", new String[]{
					"政治概念", "政治人物", "政治观点", "政治事件", "思想学说"
			});
		}

		return ResponseEntity.ok(entityListByCourse.get(course));
	}

	// 实体详情
	@GetMapping("/entity-detail")
	public ResponseEntity<?> getEntityDetail(
			@RequestParam(value = "course", defaultValue = "chinese") String course,
			@RequestParam(value = "name", defaultValue = "李白") String name
	) {
		String target = "http://open.edukg.cn/opedukg/api/typeOpen/open/infoByInstanceName";
		String param = "?course={1}&name={2}&id={3}";

		if (!isCourseValid(course))
			return ResponseEntity.badRequest().body("Invalid course code");

		EntityDetailBody response = restTemplate.getForObject(
				target+param, EntityDetailBody.class,
				course, name, getEdukgToken()
		);

		return ResponseEntity.ok(response.getData());
	}

	// 问答
	@PostMapping("/answer-for-question")
	public ResponseEntity<?> answerQuestion(
			@RequestBody GeneralInputBody body
	) {
		String target = "http://open.edukg.cn/opedukg/api/typeOpen/open/inputQuestion";

		if (!isCourseValid(body.getCourse()))
			return ResponseEntity.badRequest().body("Invalid course code");

		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("course", body.getCourse());
		requestBody.add("inputQuestion", body.getText());
		requestBody.add("id", getEdukgToken());

		QuestionAnsweringBody response = restTemplate.postForObject(
				target, requestBody, QuestionAnsweringBody.class
		);

		return ResponseEntity.ok(response.getData());
	}

	// 实体链接
	@PostMapping("/instance-linkage")
	public ResponseEntity<?> linkInstance(
			@RequestBody GeneralInputBody body
			) {
		String target = "http://open.edukg.cn/opedukg/api/typeOpen/open/linkInstance";

		if (!isCourseValid(body.getCourse()))
			return ResponseEntity.badRequest().body("Invalid course code");

		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("context", body.getText());
		requestBody.add("course", body.getCourse());
		requestBody.add("id", getEdukgToken());

		InstanceLinkageBody response = restTemplate.postForObject(
				target, requestBody, InstanceLinkageBody.class
		);

		return ResponseEntity.ok(response.getData());
	}

	// 相关习题
	@GetMapping("/exercise")
	public ResponseEntity<?> getExercise(
			@RequestParam(value = "uriName", defaultValue = "李白") String uriName
	) {
		String target = "http://open.edukg.cn/opedukg/api/typeOpen/open/questionListByUriName";
		String param = "?uriName={1}&id={2}";

		ExerciseBody response = restTemplate.getForObject(
				target+param, ExerciseBody.class,
				uriName, getEdukgToken()
		);

		ExerciseResponseBody result = new ExerciseResponseBody();
		result.setResults(response.getData());

		return ResponseEntity.ok(result);
	}

	// 关联实体
	@GetMapping("/related-entities")
	public ResponseEntity<?> getRelatedEntities(
			@RequestParam(value = "course", defaultValue = "chinese") String course,
			@RequestParam(value = "subjectName", defaultValue = "李白") String subjectName
	) {
		String target = "http://open.edukg.cn/opedukg/api/typeOpen/open/relatedsubject";

		if (!isCourseValid(course))
			return ResponseEntity.badRequest().body("Invalid course code");

		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("course", course);
		requestBody.add("subjectName", subjectName);
		requestBody.add("id", getEdukgToken());

		RelatedEntitiesBody response = restTemplate.postForObject(
				target, requestBody, RelatedEntitiesBody.class
		);

		return ResponseEntity.ok(response.getData());
	}

	// 添加收藏
	@PostMapping("/favorite")
	public ResponseEntity<?> addFavorite(
			@RequestBody FavoriteBody body,
			HttpServletRequest httpServletRequest
	) {
		Account account = accountRepository.findByName(
				jwtTokenUtil.getUsernameFromToken(
						httpServletRequest.getHeader("Authorization")
				)
		);

		if (favoriteRepository.findByCourseAndLabelAndAccount(
				body.getCourse(), body.getLabel(), account
		) != null) {
			return ResponseEntity.badRequest().body("Favorite already exists");
		}

		Favorite favorite = new Favorite();
		favorite.setAccount(account);
		favorite.setCourse(body.getCourse());
		favorite.setLabel(body.getLabel());
		favoriteRepository.save(favorite);

		return ResponseEntity.ok(body);
	}

	// 删除收藏
	@DeleteMapping("/favorite")
	public ResponseEntity<?> deleteFavorite(
			@RequestParam("course") String course,
			@RequestParam("label") String label,
			HttpServletRequest httpServletRequest
	) {
		Account account = accountRepository.findByName(
				jwtTokenUtil.getUsernameFromToken(
						httpServletRequest.getHeader("Authorization")
				)
		);

		Favorite favorite = favoriteRepository.findByCourseAndLabelAndAccount(
				course, label, account
		);
		if (favorite == null) {
			return ResponseEntity.badRequest().body("Favorite not found");
		}

		favoriteRepository.delete(favorite);

		FavoriteBody response = new FavoriteBody();
		response.setCourse(course);
		response.setLabel(label);

		return ResponseEntity.ok(response);
	}

	// 查询收藏列表
	@GetMapping("/favorite")
	public ResponseEntity<?> getFavorite(HttpServletRequest httpServletRequest) {
		Account account = accountRepository.findByName(
				jwtTokenUtil.getUsernameFromToken(
						httpServletRequest.getHeader("Authorization")
				)
		);

		Iterator<Favorite> it = favoriteRepository.findAllByAccount(account).iterator();

		ArrayList<FavoriteBody> favList = new ArrayList<>();
		while (it.hasNext()) {
			Favorite fav = it.next();
			favList.add(new FavoriteBody(fav.getCourse(), fav.getLabel()));
		}

		return ResponseEntity.ok(favList);
	}

	// 添加历史记录
	@PostMapping("history")
	public ResponseEntity<?> addHistory(
			@RequestBody FavoriteBody body,
			HttpServletRequest httpServletRequest
	) {
		Account account = accountRepository.findByName(
				jwtTokenUtil.getUsernameFromToken(
						httpServletRequest.getHeader("Authorization")
				)
		);

		Long time = new Date().getTime();

		Iterator<History> it = historyRepository.findAllByAccountOrderByTime(account).iterator();
		ArrayList<History> historyList = new ArrayList<>();
		while (it.hasNext()) {
			historyList.add(it.next());
		}

		// 避免历史记录过长，仅保留最近的50条
		if (historyList.size() >= 50) {
			historyRepository.delete(historyList.get(0));
		}

		History newHistory = new History();
		newHistory.setAccount(account);
		newHistory.setCourse(body.getCourse());
		newHistory.setLabel(body.getLabel());
		newHistory.setTime(time);
		historyRepository.save(newHistory);

		return ResponseEntity.ok(body);
	}

	// 查询历史记录
	@GetMapping("history")
	public ResponseEntity<?> getHistory(HttpServletRequest httpServletRequest) {
		Account account = accountRepository.findByName(
				jwtTokenUtil.getUsernameFromToken(
						httpServletRequest.getHeader("Authorization")
				)
		);

		Iterator<History> it = historyRepository.findAllByAccount(account).iterator();

		ArrayList<HistoryBody> historyList = new ArrayList<>();
		while (it.hasNext()) {
			History history = it.next();
			historyList.add(new HistoryBody(
					history.getLabel(), history.getCourse(), history.getTime()
			));
		}

		historyList.sort(new Comparator<HistoryBody>() {
			@Override
			public int compare(HistoryBody o1, HistoryBody o2) {
				return o2.getTime().compareTo(o1.getTime());
			}
		});

		return ResponseEntity.ok(historyList);
	}

	// --- 内部方法 --- //
	// 返回一个有效的 token，若已过期则自动重新获取
	private String getEdukgToken() {
		if (edukgExpirationTime.before(new Date()))
			requestForToken();
		return edukgToken;
	}

	// 请求一个新的 token
	private void requestForToken() {
		AppInfo appInfo = appInfoRepository.findAll().iterator().next();

		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("password", appInfo.getEdukgPassword());
		requestBody.add("phone", appInfo.getEdukgUsername());

		TokenRequestEntity response = restTemplate.postForObject(
				"http://open.edukg.cn/opedukg/api/typeAuth/user/login",
				requestBody, TokenRequestEntity.class
		);

		edukgToken = response.getId();
		edukgExpirationTime = new Date();
	}

	// 检测学科代号是否合法
	private boolean isCourseValid(final String course) {
		final String[] validCourse = {
				"chinese", "english", "math", "physics", "chemistry",
				"biology", "history", "geo", "politics"
		};
		boolean result = false;
		for (String courseRef : validCourse) {
			if (course.equals(courseRef)) {
				result = true;
				break;
			}
		}
		return result;
	}

	// 将传入的每个关键词搜索一遍，将结果拼接在一起返回
	private EntitySearchInnerBody[] searchAll(String course, String[] keys) {

		EntitySearchInnerBody[] result = new EntitySearchInnerBody[keys.length];
		String target = "http://open.edukg.cn/opedukg/api/typeOpen/open/instanceList";
		String param = "?course={1}&searchKey={2}&id={3}";

		for (int i = 0; i < keys.length; i++) {
			result[i] = restTemplate.getForObject(
					target+param, EntitySearchBody.class,
					course, keys[i], getEdukgToken()
			).getData()[0];
		}

		return result;
	}
}
