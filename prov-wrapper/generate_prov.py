#!/usr/bin/python

import sys, getopt

def generatePROV(scriptname,outfile,input_output_files):

    file_inputs_outputs = open(outfile, "w")


    import prov.model as prov
    import datetime
    g = prov.ProvDocument()

    g.set_default_namespace('http://itsc.uah.edu/')
    #document.add_namespace('itsc', 'http://itsc.uah.edu/')


    #document.get_provn()

    #g = prov.ProvDocument()

    #    prefix ex <http://example/>
    #    prefix dcterms <http://purl.org/dc/terms/>
    #    prefix foaf <http://xmlns.com/foaf/0.1/>

    #itsc = prov.Namespace('itsc', 'http://itsc.uah.edu/')  # namespaces do not need to be explicitly added to a document
    #g.add_namespace("dcterms", "http://purl.org/dc/terms/")
    #g.add_namespace("foaf", "http://xmlns.com/foaf/0.1/")
    #g.entity(itsc[scriptname])
    g.entity('e0', {'prov:type':"File",'path':scriptname})
    entity_counter=1
    for key in input_output_files:

        #ignore calls to standard python files
        if "Python27" in key:
            continue

        if input_output_files[key] == "r":
            #file_inputs_outputs.write("\nInput File: " + key )
            g.entity("e"+str(entity_counter),{'prov:type':"File",'path':key})
            g.used('e0', "e"+str(entity_counter))
            entity_counter=entity_counter+1

        if input_output_files[key] == "url":
            g.entity("e"+str(entity_counter),{'prov:type':"URL",'url':key})
            g.used('e0', "e"+str(entity_counter))
            entity_counter=entity_counter+1

        if input_output_files[key] == "w":
            #file_inputs_outputs.write("\nOutput File: " + key )
            g.entity("e"+str(entity_counter),{'prov:type':"File",'path':key})
            g.wasGeneratedBy("e"+str(entity_counter),'e0')
            entity_counter=entity_counter+1


    #file_inputs_outputs.write("\n"+g.serialize());
    file_inputs_outputs.write(g.get_provn());
    file_inputs_outputs.close()


def main(argv):


   inputfile = ''
   outputfile = ''
   scriptname = ''
   try:
      opts, args = getopt.getopt(argv,"hi:o:s:",["ifile=","ofile=","scriptname"])
   except getopt.GetoptError:
      print 'generate_prov.py -i <inputfile> -o <outputfile> -s <scriptname>'
      sys.exit(2)
   for opt, arg in opts:
      if opt == '-h':
         print 'generate_prov.py -i <inputfile> -o <outputfile> -s <scriptname>'
         sys.exit()
      elif opt in ("-i", "--ifile"):
         inputfile = arg
      elif opt in ("-o", "--ofile"):
         outputfile = arg
      elif opt in ("-s", "--scriptname"):
         scriptname = arg

   
   input_output_files={};
   content = lines = [line.strip() for line in open(inputfile)]
   for l in lines:
    input_output_files[l.split("^")[0].replace("'","")]=l.split("^")[1].replace("'","");
   

   generatePROV(scriptname,outputfile,input_output_files)


if __name__ == "__main__":
   main(sys.argv[1:])

