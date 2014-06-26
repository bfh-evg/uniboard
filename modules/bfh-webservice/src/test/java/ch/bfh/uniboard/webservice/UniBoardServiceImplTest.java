/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniBoard.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.webservice;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.Between;
import ch.bfh.uniboard.service.Constraint;
import ch.bfh.uniboard.service.Equals;
import ch.bfh.uniboard.service.Greater;
import ch.bfh.uniboard.service.GreaterEquals;
import ch.bfh.uniboard.service.In;
import ch.bfh.uniboard.service.Less;
import ch.bfh.uniboard.service.LessEquals;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.uniboard.webservice.data.AttributesDTO;
import ch.bfh.uniboard.webservice.data.BetweenDTO;
import ch.bfh.uniboard.webservice.data.EqualsDTO;
import ch.bfh.uniboard.webservice.data.GreaterDTO;
import ch.bfh.uniboard.webservice.data.GreaterEqualsDTO;
import ch.bfh.uniboard.webservice.data.InDTO;
import ch.bfh.uniboard.webservice.data.LessDTO;
import ch.bfh.uniboard.webservice.data.LessEqualsDTO;
import ch.bfh.uniboard.webservice.data.PostElementDTO;
import ch.bfh.uniboard.webservice.data.QueryDTO;
import ch.bfh.uniboard.webservice.data.ResultContainerDTO;
import ch.bfh.uniboard.webservice.data.StringValueDTO;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@RunWith(Arquillian.class)
public class UniBoardServiceImplTest {

	/**
	 * Helper method for building the in-memory variant of a deployable unit. See Arquillian for more information.
	 *
	 * @return a Java archive
	 */
	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive ja = ShrinkWrap.create(JavaArchive.class)
				.addPackage(UniBoardServiceImpl.class.getPackage())
				.addClass(PostServiceTestBean.class)
				.addClass(GetServiceTestBean.class)
				.addAsManifestResource("ejb-jar.xml", "ejb-jar.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		return ja;
	}

	@EJB(name = "UniBoardServiceImpl")
	UniBoardService service;

	@EJB(name = "PostServiceTestBean")
	PostServiceTestBean postService;

	@EJB(name = "GeterviceTestBean")
	GetServiceTestBean getService;

	public UniBoardServiceImplTest() {
	}

	@Test
	public void testPost1() {
		byte[] message = new byte[1];
		message[0] = 0x16;

		AttributesDTO adto = new AttributesDTO();
		AttributesDTO.EntryDTO entry1 = new AttributesDTO.EntryDTO();
		entry1.setKey("test");
		StringValueDTO string1 = new StringValueDTO();
		string1.setValue("test");
		entry1.setValue(string1);
		adto.getEntry().add(entry1);
		AttributesDTO.EntryDTO entry2 = new AttributesDTO.EntryDTO();
		entry2.setKey("test2");
		StringValueDTO string2 = new StringValueDTO();
		string2.setValue("test2");
		entry2.setValue(string2);
		adto.getEntry().add(entry2);

		service.post(message, adto);

		ch.bfh.uniboard.service.Post p = postService.getLastPost();

		assertEquals(2, p.getAlpha().getEntries().size());
		assertEquals(new StringValue("test"), p.getAlpha().getValue("test"));
		assertEquals(new StringValue("test2"), p.getAlpha().getValue("test2"));
		Assert.assertArrayEquals(message, p.getMessage());

	}

	/**
	 * Test if a between constraint works
	 */
	@Test
	public void testGet1() {
		//Set the input
		QueryDTO query = new QueryDTO();
		BetweenDTO constraint = new BetweenDTO();
		PostElementDTO postElement = PostElementDTO.MESSAGE;
		constraint.setPostElement(postElement);
		constraint.getKey().add("test");
		StringValueDTO string = new StringValueDTO("test2");
		constraint.setEnd(string);
		constraint.setStart(string);
		query.getBetweenOrInOrLess().add(constraint);

		//Setup the expected result
		ResultContainer expectedResult = new ResultContainer(new ArrayList<ch.bfh.uniboard.service.Post>(), new Attributes());
		getService.setFeedback(expectedResult);

		ResultContainerDTO result = service.get(query);

		assertEquals(result.getGamma().getEntry().size(), 0);
		assertEquals(result.getResult().getPost().size(), 0);

		Query resultingQuery = getService.getInput();
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof Between)) {
			Assert.fail();
		}
		Between bconstraint = (Between) resultingConstraint;
		assertEquals(((StringValue) bconstraint.getStart()).getValue(), "test2");
		assertEquals(((StringValue) bconstraint.getEnd()).getValue(), "test2");
		assertEquals(bconstraint.getKeys().get(0), "test");
		assertEquals(bconstraint.getPostElement().name(), PostElementDTO.MESSAGE.value());
	}

	/**
	 * Test if a equals constraint works
	 */
	@Test
	public void testGet2() {
		//Set the input
		QueryDTO query = new QueryDTO();
		EqualsDTO constraint = new EqualsDTO();
		PostElementDTO postElement = PostElementDTO.MESSAGE;
		constraint.setPostElement(postElement);
		constraint.getKey().add("test");
		StringValueDTO string = new StringValueDTO("test2");
		constraint.setValue(string);
		query.getBetweenOrInOrLess().add(constraint);

		//Setup the expected result
		ResultContainer expectedResult = new ResultContainer(new ArrayList<ch.bfh.uniboard.service.Post>(), new Attributes());
		getService.setFeedback(expectedResult);

		ResultContainerDTO result = service.get(query);

		assertEquals(result.getGamma().getEntry().size(), 0);
		assertEquals(result.getResult().getPost().size(), 0);

		Query resultingQuery = getService.getInput();
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof Equals)) {
			Assert.fail();
		}
		Equals bconstraint = (Equals) resultingConstraint;
		assertEquals(((StringValue) bconstraint.getValue()).getValue(), "test2");
		assertEquals(bconstraint.getKeys().get(0), "test");
		assertEquals(bconstraint.getPostElement().name(), PostElementDTO.MESSAGE.value());
	}

	/**
	 * Test if a greater constraint works
	 */
	@Test
	public void testGet3() {
		//Set the input
		QueryDTO query = new QueryDTO();
		GreaterDTO constraint = new GreaterDTO();
		PostElementDTO postElement = PostElementDTO.MESSAGE;
		constraint.setPostElement(postElement);
		constraint.getKey().add("test");
		StringValueDTO string = new StringValueDTO("test2");
		constraint.setValue(string);
		query.getBetweenOrInOrLess().add(constraint);

		//Setup the expected result
		ResultContainer expectedResult = new ResultContainer(new ArrayList<ch.bfh.uniboard.service.Post>(), new Attributes());
		getService.setFeedback(expectedResult);

		ResultContainerDTO result = service.get(query);

		assertEquals(result.getGamma().getEntry().size(), 0);
		assertEquals(result.getResult().getPost().size(), 0);

		Query resultingQuery = getService.getInput();
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof Greater)) {
			Assert.fail();
		}
		Greater bconstraint = (Greater) resultingConstraint;
		assertEquals(((StringValue) bconstraint.getValue()).getValue(), "test2");
		assertEquals(bconstraint.getKeys().get(0), "test");
		assertEquals(bconstraint.getPostElement().name(), PostElementDTO.MESSAGE.value());
	}

	/**
	 * Test if a greaterequals constraint works
	 */
	@Test
	public void testGet4() {
		//Set the input
		QueryDTO query = new QueryDTO();
		GreaterEqualsDTO constraint = new GreaterEqualsDTO();
		PostElementDTO postElement = PostElementDTO.MESSAGE;
		constraint.setPostElement(postElement);
		constraint.getKey().add("test");
		StringValueDTO string = new StringValueDTO("test2");
		constraint.setValue(string);
		query.getBetweenOrInOrLess().add(constraint);

		//Setup the expected result
		ResultContainer expectedResult = new ResultContainer(new ArrayList<ch.bfh.uniboard.service.Post>(), new Attributes());
		getService.setFeedback(expectedResult);

		ResultContainerDTO result = service.get(query);

		assertEquals(result.getGamma().getEntry().size(), 0);
		assertEquals(result.getResult().getPost().size(), 0);

		Query resultingQuery = getService.getInput();
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof GreaterEquals)) {
			Assert.fail();
		}
		GreaterEquals bconstraint = (GreaterEquals) resultingConstraint;
		assertEquals(((StringValue) bconstraint.getValue()).getValue(), "test2");
		assertEquals(bconstraint.getKeys().get(0), "test");
		assertEquals(bconstraint.getPostElement().name(), PostElementDTO.MESSAGE.value());
	}

	/**
	 * Test if a in constraint works
	 */
	@Test
	public void testGet5() {
		//Set the input
		QueryDTO query = new QueryDTO();
		InDTO constraint = new InDTO();
		PostElementDTO postElement = PostElementDTO.MESSAGE;
		constraint.setPostElement(postElement);
		constraint.getKey().add("test");
		StringValueDTO string = new StringValueDTO("test2");
		constraint.getElement().add(string);
		query.getBetweenOrInOrLess().add(constraint);

		//Setup the expected result
		ResultContainer expectedResult = new ResultContainer(new ArrayList<ch.bfh.uniboard.service.Post>(), new Attributes());
		getService.setFeedback(expectedResult);

		ResultContainerDTO result = service.get(query);

		assertEquals(result.getGamma().getEntry().size(), 0);
		assertEquals(result.getResult().getPost().size(), 0);

		Query resultingQuery = getService.getInput();
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof In)) {
			Assert.fail();
		}
		In bconstraint = (In) resultingConstraint;
		assertEquals(((StringValue) bconstraint.getSet().get(0)).getValue(), "test2");
		assertEquals(bconstraint.getKeys().get(0), "test");
		assertEquals(bconstraint.getPostElement().name(), PostElementDTO.MESSAGE.value());
	}

	/**
	 * Test if a less constraint works
	 */
	@Test
	public void testGet6() {
		//Set the input
		QueryDTO query = new QueryDTO();
		LessDTO constraint = new LessDTO();
		PostElementDTO postElement = PostElementDTO.MESSAGE;
		constraint.setPostElement(postElement);
		constraint.getKey().add("test");
		StringValueDTO string = new StringValueDTO("test2");
		constraint.setValue(string);
		query.getBetweenOrInOrLess().add(constraint);

		//Setup the expected result
		ResultContainer expectedResult = new ResultContainer(new ArrayList<ch.bfh.uniboard.service.Post>(), new Attributes());
		getService.setFeedback(expectedResult);

		ResultContainerDTO result = service.get(query);

		assertEquals(result.getGamma().getEntry().size(), 0);
		assertEquals(result.getResult().getPost().size(), 0);

		Query resultingQuery = getService.getInput();
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof Less)) {
			Assert.fail();
		}
		Less bconstraint = (Less) resultingConstraint;
		assertEquals(((StringValue) bconstraint.getValue()).getValue(), "test2");
		assertEquals(bconstraint.getKeys().get(0), "test");
		assertEquals(bconstraint.getPostElement().name(), PostElementDTO.MESSAGE.value());
	}

	/**
	 * Test if a lessequals constraint works
	 */
	@Test
	public void testGet7() {
		//Set the input
		QueryDTO query = new QueryDTO();
		LessEqualsDTO constraint = new LessEqualsDTO();
		PostElementDTO postElement = PostElementDTO.MESSAGE;
		constraint.setPostElement(postElement);
		constraint.getKey().add("test");
		StringValueDTO string = new StringValueDTO("test2");
		constraint.setValue(string);
		query.getBetweenOrInOrLess().add(constraint);

		//Setup the expected result
		ResultContainer expectedResult = new ResultContainer(new ArrayList<ch.bfh.uniboard.service.Post>(), new Attributes());
		getService.setFeedback(expectedResult);

		ResultContainerDTO result = service.get(query);

		assertEquals(result.getGamma().getEntry().size(), 0);
		assertEquals(result.getResult().getPost().size(), 0);

		Query resultingQuery = getService.getInput();
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof LessEquals)) {
			Assert.fail();
		}
		LessEquals bconstraint = (LessEquals) resultingConstraint;
		assertEquals(((StringValue) bconstraint.getValue()).getValue(), "test2");
		assertEquals(bconstraint.getKeys().get(0), "test");
		assertEquals(bconstraint.getPostElement().name(), PostElementDTO.MESSAGE.value());
	}

	/**
	 * Test if the resultcontainer is translated correctly
	 */
	@Test
	public void testGet8() {
		//Set the input
		QueryDTO query = new QueryDTO();
		LessEqualsDTO constraint = new LessEqualsDTO();
		PostElementDTO postElement = PostElementDTO.MESSAGE;
		constraint.setPostElement(postElement);
		constraint.getKey().add("test");
		StringValueDTO string = new StringValueDTO("test2");
		constraint.setValue(string);
		query.getBetweenOrInOrLess().add(constraint);

		//Setup the expected result
		List<ch.bfh.uniboard.service.Post> posts = new ArrayList<>();
		ch.bfh.uniboard.service.Post post = new ch.bfh.uniboard.service.Post();
		post.setAlpha(new Attributes());
		post.getAlpha().add("alpha", new StringValue("alpha"));
		post.setBeta(new Attributes());
		post.getBeta().add("beta", new StringValue("beta"));
		byte[] message = new byte[1];
		message[0] = 0x16;
		post.setMessage(message);
		posts.add(post);

		Attributes attributes = new Attributes();
		attributes.add("gamma", new StringValue("gamma"));

		ResultContainer expectedResult = new ResultContainer(posts, attributes);
		getService.setFeedback(expectedResult);

		ResultContainerDTO result = service.get(query);

		assertEquals(result.getGamma().getEntry().size(), 1);
		assertEquals(result.getGamma().getEntry().get(0).getKey(), "gamma");
		assertEquals(((StringValueDTO) result.getGamma().getEntry().get(0).getValue()).getValue(), "gamma");
		assertEquals(result.getResult().getPost().size(), 1);
		Assert.assertArrayEquals(result.getResult().getPost().get(0).getMessage(), message);
		assertEquals(result.getResult().getPost().get(0).getAlpha().getEntry().get(0).getKey(), "alpha");
		assertEquals(((StringValueDTO) result.getResult().getPost().get(0).getAlpha().getEntry().get(0).getValue()).getValue(), "alpha");
		assertEquals(result.getResult().getPost().get(0).getBeta().getEntry().get(0).getKey(), "beta");
		assertEquals(((StringValueDTO) result.getResult().getPost().get(0).getBeta().getEntry().get(0).getValue()).getValue(), "beta");
	}
}
