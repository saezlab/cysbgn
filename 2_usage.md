---
layout: page
title: Usage
---

## **Using CySBGN**

CySBGN tutorial - [pdf](/cysbgn/public/tutorial.pdf)


### **How to import**

> File -> Import-> Network (Multiple File Types) 

After CySBGN is installed the user can import a SBGN diagram by clicking in File -> Import -> Network (Multiple File Types) and then select the respective SBGN-ML file (as shown in the figure below). 

Example file can be [downloaded here](http://sourceforge.net/projects/cysbgn/files/SBGN-ML_Example_Files/glycolysis.sbgn/download) and a few other SBGN diagrams files can be [downloaded here](http://sourceforge.net/projects/cysbgn/files/SBGN-ML_Example_Files/).

<img src="/cysbgn/public/first.import.screenshot.png" alt="CySBGN example" style="width:500px;height:500px;">

After the diagram is successfully imported during the first import the user will be prompted with an information dialog regarding the rendering limitations of Cytoscape that affects the visualization of the SBGN diagrams. This dialog can be disable by ticking the Don’t show me this again box. 

<img src="/cysbgn/public/limitation.panel.screenshot.png" alt="CySBGN example" style="width:500px;height:500px;">


### **Simplify Diagram**

> Plugins -> CySBGN -> Create Simplified Network...

To render the diagrams as close as possible to the original, CySBGN creates some auxiliary nodes and edges to overlap the Cytoscape limitations (e.g. invisible ports to allow a edge to connect with another edge). Although, this auxiliary shapes represent some limitations to the correct operability of Cytoscape analysis or display methods (e.g. in a shortest path algorithm the auxiliary nodes are considered as one step as well).

To avoid this a simplification method was created that removes these auxiliary shapes as well as complex or compartments SBGN entities. The image below shows how a user can preform the simplification of a previously imported SBGN diagram.

<img src="/cysbgn/public/simplification_1.png" alt="CySBGN example" style="width:400px;height:500px;">

After the simplification a view is generated and can be seen side-by-side with the original SBGN diagram.

<img src="/cysbgn/public/simplification_2.png" alt="CySBGN example" style="width:400px;height:500px;">

At this point a Layout algorithm can also be applied.

<img src="/cysbgn/public/simplification_3.png" alt="CySBGN example" style="width:400px;height:500px;">


### **SBGN-ML validation**

> Plugins -> CySBGN -> Validate network SBGN-ML...

CySBGN by using libSBGN enables the users to perform a syntactic validation of SBGN-ML documents. The validation function is accessible under the CySBGN menu in the Plugins menu and runs a syntax validation of the SBGN-ML file used to import currently selected SBGN-ML diagram. 

<img src="/cysbgn/public/validate.screenshot.png" alt="CySBGN example" style="width:400px;height:500px;">

Any issue found with the SBGN-ML file is listed in the Data Panel (bottom panel) in the CySBGN-Validation tab.


### **Automatic generation of SBGN diagrams from SBML models**

> Plugins -> CySBGN -> SBML to SBGN... 

After installing CySBGN and CySBML plug-ins correctly (check CySBGN tutorial for detailed instructions) the user can complement SBML model visualization with an automated generated SBGN diagram of the model. 

Through **Plugins -> CySBGN -> SBML to SBGN...** the user can select a SBML model and generate the respective SBGN diagram.

**The conversion process may take awhile**

<img src="/cysbgn/public/convert_cysbgn_1.png" alt="CySBGN example" style="width:400px;height:500px;">

Since we are dealing with automated generation of diagrams the layout may need some manual correction. Here the overlapping nodes were moved to be completely visible. SBML model [link](/cysbgn/public/BIOMD0000000422.xml). 

Selected node information is visible in the Results Panel (right side panel) in CySBML tab, also Data Panel (bottom panel) shows SBML and SBGN node’s attributes.

> Some attributes may be hidden. Click Select All Attributes button to visualize all the attributes

The user can also take advantage of [BioModels](http://www.ebi.ac.uk/biomodels-main/) database as a resource of SBML models (check CySBGN tutorial for more details). 

The example model mentioned above can be downloaded directly from the links below:
> [SMBL model file](/cysbgn/public/BIOMD0000000422.xml)
> [BioModels webpage of the model](http://www.ebi.ac.uk/biomodels-main/BIOMD0000000422)
> [SBGN diagram file](/cysbgn/public/BIOMD0000000422.sbgn)


### **CySBGN rendering comparison**

CySBGN rendering can be compared [here](http://libsbgn.sourceforge.net/render_comparison/) with other SBGN complaint tools. We re-used an extensive set of test cases covering the three supplementary languages of SBGN to validate the rendering of CySBGN. 

<img src="/cysbgn/public/rendering_comparison.png" alt="CySBGN example" style="width:400px;height:500px;">

### **FAQ**
Can I generate a SBGN diagram file (SBGN-ML file format) from a SBML model?
Yes. In CySBGN menu (under plugins menu) select SBML to SBGN, pick the desired SBML model and the SBGN file will be automatically generated and stored with the same name as the SBML file but with .sbgn extension. 

Help me filling this section! <a href="mailto:emanuel@ebi.ac.uk">Mail me your questions.</a></i>