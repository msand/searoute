/**
 * 
 */
package eu.europa.ec.eurostat.searoute;

import java.util.Collection;
import java.util.HashSet;

import org.locationtech.jts.geom.LineString;

import eu.europa.ec.eurostat.jgiscotools.algo.line.DouglasPeuckerRamerFilter;
import eu.europa.ec.eurostat.jgiscotools.feature.FeatureUtil;
import eu.europa.ec.eurostat.jgiscotools.graph.algo.ConnexComponents;
import eu.europa.ec.eurostat.jgiscotools.graph.algo.GraphSimplify;
import eu.europa.ec.eurostat.jgiscotools.io.GeoJSONUtil;

/**
 * Some functions to build maritime networks at different resolutions.
 * 
 * @author julien Gaffuri
 *
 */
public class MarnetBuilding {
	public static double[] resDegs = new double[] { 0.5, 0.25, 0.1, 0.05, 0.025 };

	//TODO
	//add port connections from GISCO DB
	//for thin triangles (with short height), remove the longest segment
	//publish - document
	//ensure no land intersection ?

	public static Collection<LineString> make(double res, String... datasources) {
		//load input lines
		Collection<LineString> lines = new HashSet<LineString>();

		//data sources preparation
		for(String ds : datasources) {
			System.out.println(" preparing "+ds);
			Collection ds_ = FeatureUtil.featuresToGeometries( GeoJSONUtil.load(ds));
			ds_ = prepare(ds_, res);
			lines.addAll(ds_);
		}

		System.out.println("Total before integration: " + lines.size());

		lines = GraphSimplify.planifyLines(lines);						System.out.println(lines.size() + " planifyLines");
		lines = GraphSimplify.lineMerge(lines);							System.out.println(lines.size() + " lineMerge");
		lines = DouglasPeuckerRamerFilter.get(lines, res);						System.out.println(lines.size() + " filterGeom");
		lines = GraphSimplify.removeSimilarDuplicateEdges(lines, res);	System.out.println(lines.size() + " removeSimilarDuplicateEdges");
		//lines = MeshSimplification.dtsePlanifyLines(lines, res);				System.out.println(lines.size() + " dtsePlanifyLines");
		lines = GraphSimplify.lineMerge(lines);							System.out.println(lines.size() + " lineMerge");
		lines = GraphSimplify.planifyLines(lines);						System.out.println(lines.size() + " planifyLines");
		lines = GraphSimplify.lineMerge(lines);							System.out.println(lines.size() + " lineMerge");
		//lines = MeshSimplification.dtsePlanifyLines(lines, res);				System.out.println(lines.size() + " dtsePlanifyLines");
		lines = GraphSimplify.lineMerge(lines);							System.out.println(lines.size() + " lineMerge");
		lines = GraphSimplify.planifyLines(lines);						System.out.println(lines.size() + " planifyLines");
		lines = GraphSimplify.lineMerge(lines);							System.out.println(lines.size() + " lineMerge");
		//lines = MeshSimplification.dtsePlanifyLines(lines, res);				System.out.println(lines.size() + " dtsePlanifyLines");
		lines = GraphSimplify.lineMerge(lines);							System.out.println(lines.size() + " lineMerge");
		lines = GraphSimplify.resPlanifyLines(lines, res*0.01, false);			System.out.println(lines.size() + " resPlanifyLines");
		lines = GraphSimplify.lineMerge(lines);							System.out.println(lines.size() + " lineMerge");
		lines = GraphSimplify.resPlanifyLines(lines, res*0.01, false);			System.out.println(lines.size() + " resPlanifyLines");

		//run with -Xss4m
		lines = ConnexComponents.keepOnlyLargestGraphConnexComponents(lines, 50);	System.out.println(lines.size() + " keepOnlyLargestGraphConnexComponents");

		return lines;
	}

	private static Collection<LineString> prepare(Collection<LineString> lines, double res) {
		lines = GraphSimplify.planifyLines(lines);						System.out.println(lines.size() + " planifyLines");
		lines = GraphSimplify.lineMerge(lines);							System.out.println(lines.size() + " lineMerge");
		lines = DouglasPeuckerRamerFilter.get(lines, res);						System.out.println(lines.size() + " filterGeom");
		lines = GraphSimplify.removeSimilarDuplicateEdges(lines, res);	System.out.println(lines.size() + " removeSimilarDuplicateEdges");
		lines = GraphSimplify.collapseTooShortEdgesAndPlanifyLines(lines, res, true, true);				System.out.println(lines.size() + " dtsePlanifyLines");
		lines = GraphSimplify.resPlanifyLines(lines, res*0.01, false);			System.out.println(lines.size() + " resPlanifyLines");
		return lines;
	}

}

