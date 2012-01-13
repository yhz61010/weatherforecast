import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.beans.StringBean;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.RegexFilter;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.BodyTag;
import org.htmlparser.tags.HeadTag;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;
import org.htmlparser.visitors.NodeVisitor;
import org.htmlparser.visitors.ObjectFindingVisitor;

/**
 * @author Michael Leo
 * @version 2010/08/11
 */
public class ParseUtils {
	private static final String DEFAULT_ENCODE = "UTF-8";

	private static final String NEWLINE = "\r\n";

	private static String getContent(HttpEntity entity, String encode)
			throws Exception {
		if (entity != null) {
			entity = new BufferedHttpEntity(entity);
			return EntityUtils.toString(entity, encode);
		} else {
			System.err.println("Entity is null.");
			return null;
		}
	}

	public static String getContent(String url, String encode) throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);

		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		return getContent(entity, encode);
	}

	public static String getContent(String url) throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);

		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		return getContent(entity, DEFAULT_ENCODE);
	}

	/* =================================================== */

	public static <T extends Tag> void getInnerHTML(String url, Class<T> clazz)
			throws ParserException {
		Parser parser = new Parser(url);
		NodeFilter filter = new NodeClassFilter(clazz);
		NodeList list = parser.extractAllNodesThatMatch(filter);
		for (int i = 0; i < list.size(); i++) {
			@SuppressWarnings("unchecked")
			T node = (T) list.elementAt(i);
			System.out.println((i + 1) + ".\t" + node.toPlainTextString());
		}
	}

	public static String[] getElementByTagName(String url, String tag)
			throws ParserException {
		Parser parser = new Parser(url);
		NodeFilter filter = new TagNameFilter(tag);
		NodeList list = parser.extractAllNodesThatMatch(filter);
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.elementAt(i).toPlainTextString().trim();
		}
		return array;
	}

	public static String getStringElementByTagName(String url, String tag)
			throws ParserException {
		Parser parser = new Parser(url);
		NodeFilter filter = new TagNameFilter(tag);
		NodeList list = parser.extractAllNodesThatMatch(filter);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.elementAt(i).toHtml());
			sb.append(NEWLINE);
		}
		return sb.toString();
	}

	public static <T extends Tag> void getTagAttribute(String url,
			Class<T> clazz, String attr) throws ParserException {
		Parser parser = new Parser(url);
		NodeFilter filter = new NodeClassFilter(clazz);
		NodeList list = parser.extractAllNodesThatMatch(filter);
		for (int i = 0; i < list.size(); i++) {
			@SuppressWarnings("unchecked")
			T node = (T) list.elementAt(i);
			System.out.println((i + 1) + ".\t" + node.getAttribute(attr));
		}
	}

	public static void getRegexContent(String url, String regex)
			throws ParserException {
		// String textContents = getContentUsingStringBean(url);
		// Parser parser = Parser.createParser(textContents, charset);
		Parser parser = new Parser(url);
		RegexFilter filter = new RegexFilter(regex);
		NodeList list = parser.extractAllNodesThatMatch(filter);
		for (int i = 0; i < list.size(); i++) {
			System.out.println((i + 1) + ".\t"
					+ list.elementAt(i).toPlainTextString());
		}
	}

	public static String[] getStringContent(String url, String str)
			throws ParserException {
		Parser parser = new Parser(url);
		StringFilter filter = new StringFilter(str);
		NodeList list = parser.extractAllNodesThatMatch(filter);
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.elementAt(i).toPlainTextString().trim();
		}
		return array;
	}

	public static void getElementByAttr(String url, String attr)
			throws ParserException {
		Parser p = new Parser(url);

		HasAttributeFilter att = new HasAttributeFilter(attr);
		NodeList list = p.extractAllNodesThatMatch(att);
		for (int i = 0; i < list.size(); i++) {
			System.out.println((i + 1) + ".\t" + list.elementAt(i).toHtml());
		}
	}

	public static String getElementByAttr(String url, String attr, String value)
			throws ParserException {
		Parser p = new Parser(url);

		HasAttributeFilter att = new HasAttributeFilter(attr, value);
		NodeList list = p.extractAllNodesThatMatch(att);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.elementAt(i).toHtml());
			sb.append(NEWLINE);
		}
		return sb.toString();
	}

	public static void getHtmlPage(String url) throws ParserException {
		Parser parser = new Parser(url);
		HtmlPage htmlPage = new HtmlPage(parser);
		parser.visitAllNodesWith(htmlPage);

		String textInPage = htmlPage.getTitle();
		System.out.println(textInPage);

		NodeList nodelist = htmlPage.getBody();
		System.out.print(nodelist.asString().trim());
	}

	/* =================================================== */

	public static String getTableInfo(String html, String charset)
			throws ParserException {
		Parser parser = Parser.createParser(html, charset);
		NodeList nodeList = null;

		NodeFilter tableFilter = new NodeClassFilter(TableTag.class);
		OrFilter lastFilter = new OrFilter();
		lastFilter.setPredicates(new NodeFilter[] { tableFilter });
		nodeList = parser.parse(lastFilter);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i <= nodeList.size(); i++) {
			if (nodeList.elementAt(i) instanceof TableTag) {
				TableTag tag = (TableTag) nodeList.elementAt(i);
				TableRow[] rows = tag.getRows();

				for (int j = 0; j < rows.length; j++) {
					TableRow tr = rows[j];
					TableColumn[] td = tr.getColumns();
					for (int k = 0; k < td.length; k++) {
						if (StringUtils.isBlank(td[k].toPlainTextString()
								.trim())) {
							continue;
						}
						sb.append(td[k].toPlainTextString().trim());
						sb.append(NEWLINE);
					}
				}
			}
		}
		return sb.toString();
	}

	/* =================================================== */

	public static void getImgTag(String url) throws ParserException {
		Parser parser = new Parser(url);
		ImageTag imgTag;
		ObjectFindingVisitor visitor = new ObjectFindingVisitor(ImageTag.class);
		parser.visitAllNodesWith(visitor);
		Node[] nodes = visitor.getTags();
		for (Node node : nodes) {
			imgTag = (ImageTag) node;
			System.out.println(imgTag.getImageURL());
			// System.out.println("location=" + imgTag.extractImageLocn());
			// System.out.println(imgTag.getAttribute("src"));
		}
	}

	public static void getImgNodeFilter(String url) throws ParserException {
		Parser parser = new Parser(url);
		NodeFilter filter = new TagNameFilter("IMG");
		NodeList list = parser.extractAllNodesThatMatch(filter);
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.elementAt(i).toHtml());
		}
	}

	public static void getLinkTag(String url) throws ParserException {
		Parser parser = new Parser(url);
		NodeFilter filter = new NodeClassFilter(LinkTag.class);
		NodeList list = parser.extractAllNodesThatMatch(filter);
		for (int i = 0; i < list.size(); i++) {
			LinkTag node = (LinkTag) list.elementAt(i);
			System.out.println((i + 1) + ".\t" + node.extractLink());
		}
	}

	public static void getLinkNodeFilter(String url) throws ParserException {
		Parser parser = new Parser(url);
		NodeFilter filter = new TagNameFilter("A");
		NodeList list = parser.extractAllNodesThatMatch(filter);
		for (int i = 0; i < list.size(); i++) {
			System.out.println((i + 1) + ".\t" + list.elementAt(i).toHtml());
		}
	}

	public static void visitorAllTag(String url) throws ParserException {
		Parser parser = new Parser(url);
		NodeVisitor visitor = new NodeVisitor() {
			public void visitTag(Tag tag) {
				System.out.println("Tag name: " + tag.getTagName()
						+ "\nClass: " + tag.getClass());
			}

		};
		parser.visitAllNodesWith(visitor);
	}

	public static void getSpecifiedTagVisitor(String url)
			throws ParserException {
		Parser parser = new Parser(url);
		NodeVisitor visitor = new NodeVisitor() {
			public void visitTag(Tag tag) {
				if (tag instanceof HeadTag) {
					System.out.println("HeadTag : Tag name is :"
							+ tag.getTagName() + "\nClass is :"
							+ tag.getClass() + "\nText is :" + tag.getText());
				} else if (tag instanceof TitleTag) {
					System.out.println("TitleTag : Tag name is :"
							+ tag.getTagName() + "\nClass is :"
							+ tag.getClass() + "\nText is :" + tag.getText());

				} else if (tag instanceof LinkTag) {
					System.out.println("LinkTag : Tag name is :"
							+ tag.getTagName() + "\nClass is :"
							+ tag.getClass() + "\nText is :" + tag.getText()
							+ "\ngetAttribute is :" + tag.getAttribute("href"));
				} else {
					System.out.println("Other: Tag name is :"
							+ tag.getTagName() + "\nClass is :"
							+ tag.getClass() + "\nText is :" + tag.getText());
				}

			}

		};
		parser.visitAllNodesWith(visitor);
	}

	// -----------------------------------------------------------------

	/**
	 * 使用HtmlParser抓去网页内容: 要抓去页面的内容最方便的方法就是使用StringBean. 里面有几个控制页面内容的几个参数.
	 * 在后面的代码中会有说明. Htmlparser包中还有一个示例StringExtractor 里面有个直接得到内容的方法,
	 * 其中也是使用了StringBean . 另外直接解析Parser的每个标签也可以的.
	 */
	public static String getContentUsingStringBean(String url) {
		StringBean sb = new StringBean();
		// 是否显示web页面的连接(Links)
		sb.setLinks(false);
		// Generally, this method should be set true.
		sb.setReplaceNonBreakingSpaces(true);
		// 如果是true的话把一系列空白字符用一个字符替代.
		sb.setCollapse(true);
		// All of the parameters have been set above should be before setURL
		// method.
		sb.setURL(url);

		return sb.getStrings();
	}

	// Keep the original format
	public static void getContentUsingParser(String url) throws ParserException {
		NodeList nl;
		Parser p = new Parser(url);
		nl = p.parse(new NodeClassFilter(BodyTag.class));
		BodyTag bt = (BodyTag) nl.elementAt(0);
		// 保留原来的内容格式. 包含js代码
		System.out.println(bt.toPlainTextString());
	}

}
