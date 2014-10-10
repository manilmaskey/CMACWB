import glob
import sys
import pprint
import linecache
from inspect import getmembers
import os
file=0;

input_output_files={};

def trace_lines(frame,event,arg):
    global file
    global input_output_files

     
    co = frame.f_code
    func_name = co.co_name
    line_no = frame.f_lineno
   
    filename = co.co_filename
    line = linecache.getline(filename, line_no)
    
    #file.write("\nExecuting " + str(line_no) +" line ["+line.strip()+"] from function "+func_name + " in file " + filename )
    #file.write("\nLocal Variables:\n")
    for key in frame.f_locals :
        try:
            
            if key=="self":
                continue
            else:
                key_value=str(frame.f_locals[key])       
        except Exception as inst:
            #print "\n Key = " + key
            #print type(inst)
            #print inst.args
            #print inst
            #print frame.f_locals
            continue

        #file.write("\n"+key +"="+key_value)
        if "open file" in key_value:
            pos1 = key_value.find("'")
            pos2 = key_value.find("'", pos1+1)
          
            opened_filename=key_value[pos1+1:pos2]
            if (opened_filename==".") or (opened_filename=="sys.stdout") or (opened_filename=="<stderr>"):
                continue    
            
            if "/dev/null" in opened_filename:
                continue

            if "mode 'r'" in key_value:
                 #file.write("\n" + opened_filename+" file opened for reading\n")
                 input_output_files[opened_filename]="r";
            if "mode 'w'" in key_value:
                 #file.write("\n" + opened_filename+" file opened for writing\n")
                 input_output_files[opened_filename]="w";
            if "mode 'a'" in key_value:
                 #file.write("\n" + opened_filename+" file opened for writing\n")
                 input_output_files[opened_filename]="w";   

        if key=="fname":
            if "read" in func_name:
                input_output_files[key_value]="r";
            if "load" in func_name:
                input_output_files[key_value]="r";

            if "write" in func_name:
                input_output_files[key_value]="w";

            if "save" in func_name:
                input_output_files[key_value]="w";
        
        if func_name == "urlretrieve":
            if key=="url":
                input_output_files[key_value]="url";
            if key=="filename":
                input_output_files[key_value]="w";
        
        if func_name == "save":
            if key=="filename":
                input_output_files[key_value]="w";
            #if func_name=="imread":
                #input_output_files[key_value]="r";
            #if func_name=="imsave":
                 #input_output_files[key_value]="w";

    #file.write("\n=========================\n")

def trace_calls(frame,event,arg):
    global file

    co = frame.f_code
    func_name = co.co_name
    
    func_line_no = frame.f_lineno
    func_filename = co.co_filename
    caller = frame.f_back
    if hasattr(caller,"f_lineno"):
        caller_line_no = caller.f_lineno
        caller_filename = caller.f_code.co_filename
        #file.write("\nCall to " + func_name+" on line "+ str(func_line_no)+" of "+func_filename+"  from line "+ str(caller_line_no)+" of " + caller_filename)
    return trace_lines

def dummy_trace(frame,event,arg):
    return

def stop_trace():
    sys.settrace(dummy_trace)


def trace():

    global file
    #file = open(os.path.dirname(os.path.realpath(__file__))+"/trace_log.txt", "w")

    sys.settrace(trace_calls)
    #print input_output_files
    
   

                
def generatePROV(scriptname,input_output_files):
    #import needed modules
    import urllib
    import urllib2

    directory = os.path.dirname(os.path.realpath(__file__))+"/provenance"
    if not os.path.exists(directory):
        os.makedirs(directory)

    url = "http://54.208.76.40/prov-wrapper/generate_provn.php"
    post_data_dictionary = {'inputs_outputs':input_output_files,'scriptname':scriptname}
    post_data_encoded = urllib.urlencode(post_data_dictionary)
    request_object = urllib2.Request(url, post_data_encoded)
    response = urllib2.urlopen(request_object)
    provn_string = response.read()
    file_inputs_outputs = open(os.path.dirname(os.path.realpath(__file__))+"/provenance/"+scriptname+".provn", "w")
    file_inputs_outputs.write(provn_string);
    file_inputs_outputs.close() 
    with open(os.path.dirname(os.path.realpath(__file__))+"/provenance/"+scriptname+".provn") as input:
    # Read non-empty lines from input file
        lines = [line for line in input if line.strip()]
        #print lines
    with open(os.path.dirname(os.path.realpath(__file__))+"/provenance/"+scriptname+".provn", "w") as output:
        for line in lines:
            output.write(line.replace('\r\n', '\n').replace('\r', '\n'))

    #print provn_string

    
    url = "http://54.208.76.40/prov-wrapper/generate_svg.php"
    post_data_dictionary = {'provn':provn_string}
    post_data_encoded = urllib.urlencode(post_data_dictionary)
    request_object = urllib2.Request(url, post_data_encoded)
    response = urllib2.urlopen(request_object)
    svg_string = response.read()
    file_inputs_outputs = open(os.path.dirname(os.path.realpath(__file__))+"/provenance/"+scriptname+".svg", "w")
    file_inputs_outputs.write(svg_string);
    file_inputs_outputs.close() 


    url = "http://54.208.76.40/prov-wrapper/generate_png.php"
    post_data_dictionary = {'svg':svg_string}
    post_data_encoded = urllib.urlencode(post_data_dictionary)
    request_object = urllib2.Request(url, post_data_encoded)
    response = urllib2.urlopen(request_object)
    png_string = response.read()
    file_inputs_outputs = open(os.path.dirname(os.path.realpath(__file__))+"/provenance/"+scriptname+".png", "wb")
    file_inputs_outputs.write(png_string);
    file_inputs_outputs.close() 

    os.remove(os.path.dirname(os.path.realpath(__file__))+"/trace_"+scriptname);
    os.remove(os.path.dirname(os.path.realpath(__file__))+"/trace_execution.pyc");
    os.remove(os.path.dirname(os.path.realpath(__file__))+"/trace_execution.py");

    return



#trace_execution.generatePROV(trace_execution.input_output_files)