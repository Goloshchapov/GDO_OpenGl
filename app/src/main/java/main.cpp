//
//  main.cpp
//  objFileConverter
//
//  Created by Денис Денис on 27.05.14.
//  Copyright (c) 2014 GDO apps. All rights reserved.
//

#include <iostream>
#include <fstream>
#include <string>
using namespace std;


// Model Structure
typedef struct Model
{
    int vertices;
    int positions;
    int texels;
    int normals;
    int faces;
}
Model;


typedef struct newModel
{
    int vertices;
    int positions;
    int texels;
    int normals;
    int faces;
}
newModel;

Model getOBJinfo(string fp)
{
    // 2
    Model model = {0};
    
    // 3
    // Open OBJ file
    ifstream inOBJ;
    inOBJ.open(fp);
    if(!inOBJ.good())
    {
        cout << "ERROR OPENING OBJ FILE" << endl;
        exit(1);
    }
    
    // 4
    // Read OBJ file
    while(!inOBJ.eof())
    {
        // 5
        string line;
        getline(inOBJ, line);
        string type = line.substr(0,2);
        
        // 6
        if(type.compare("v ") == 0)
            model.positions++;
        
        else if(type.compare("vt") == 0)
            model.texels++;
      
        else if(type.compare("vn") == 0)
            model.normals++;
        
        else if(type.compare("f ") == 0)
            model.faces++;
         
    }
    
    // 7
    model.vertices = model.faces*3;
     cout<<model.positions<<endl;
      cout<<model.texels<<endl;
      cout<<model.normals<<endl;
      cout<<model.faces<<endl;
    // 8
    // Close OBJ file
    inOBJ.close();
    
    // 9
    return model;
}



void extractOBJdata(string fp, float positions[][3], float texels[][2], float normals[][3], int faces[][9])
{
    // Counters
    int p = 0;
    int t = 0;
    int n = 0;
    int f = 0;
    
    // Open OBJ file
    ifstream inOBJ;
    inOBJ.open(fp);
    if(!inOBJ.good())
    {
        cout << "ERROR OPENING OBJ FILE" << endl;
        exit(1);
    }
    
    // Read OBJ file
    while(!inOBJ.eof())
    {
        string line;
        getline(inOBJ, line);
        string type = line.substr(0,2);
        
        // Positions
        if(type.compare("v ") == 0)
        {
        
            // Copy line for parsing
            char* l = new char[line.size()+1];
            memcpy(l, line.c_str(), line.size()+1);
            
            // 2
            // Extract tokens
            strtok(l, " ");
            for(int i=0; i<3; i++)
                positions[p][i] = atof(strtok(NULL, " "));
            
            // 3
            // Wrap up
            delete[] l;
            p++;
   //      
        }
        
        // Texels
        else if(type.compare("vt") == 0)
        {
            char* l = new char[line.size()+1];
            memcpy(l, line.c_str(), line.size()+1);
            
            strtok(l, " ");
            for(int i=0; i<2; i++)
                texels[t][i] = atof(strtok(NULL, " "));
            
            delete[] l;
            t++;
            
        }
        
        // Normals
        else if(type.compare("vn") == 0)
        {
            char* l = new char[line.size()+1];
            memcpy(l, line.c_str(), line.size()+1);
            
            strtok(l, " ");
            for(int i=0; i<3; i++)
                normals[n][i] = atof(strtok(NULL, " "));
            
            delete[] l;
            n++;
            
        }
        
        // Faces
        else if(type.compare("f ") == 0)
        {
            char* l = new char[line.size()+1];
            memcpy(l, line.c_str(), line.size()+1);
            
            strtok(l, " ");
            for(int i=0; i<9; i++)
                faces[f][i] = atof(strtok(NULL, " /"));
            
            delete[] l;
            f++;
        }
    }
    
    // Close OBJ file
    inOBJ.close();
    
    
}



void rewritePositions(Model model, int faces[][9], float positions[][3], float newposition[])
{
    int p =0;
    for(int i=0; i<model.faces; i++)
    {
        int vA = faces[i][0] - 1;
        int vB = faces[i][3] - 1;
        int vC = faces[i][6] - 1;
        
        
                
        newposition[p]= positions[vA][0];
      
        
         p++;
        newposition[p]= positions[vA][1];
        
        p++;
        newposition[p]= positions[vA][2];
       
        p++;
        newposition[p]= positions[vB][0];
         p++;
        newposition[p]= positions[vB][1];
         p++;
        newposition[p]= positions[vB][2];
         p++;
        newposition[p]= positions[vC][0];
         p++;
        newposition[p]= positions[vC][1];
         p++;
        newposition[p]= positions[vC][2];
         p++;
   
    }
   
    
}

void reWriteUV( Model model, int faces[][9], float texels[][2], float newUV[])
{
        
    int p =0;
    for(int i=0; i<model.faces; i++)
    {
        int vtA = faces[i][1] - 1;
        int vtB = faces[i][4] - 1;
        int vtC = faces[i][7] - 1;
        
        newUV[p] = texels[vtA][0];
        p++;
        newUV[p] =1- texels[vtA][1];
        p++;
        newUV[p] = texels[vtB][0];
        p++;
        newUV[p] =1- texels[vtB][1];
        p++;
        newUV[p] = texels[vtC][0];
        p++;
        newUV[p] =1- texels[vtC][1];
        p++; 
    }
 
   
}

void reWriteNormals(Model model, int faces[][9], float normals[][3],float newNormals[])
{
    int p =0;
    for(int i=0; i<model.faces; i++)
    {
        int vnA = faces[i][2] - 1;
        int vnB = faces[i][5] - 1;
        int vnC = faces[i][8] - 1;
        
        newNormals[p]= normals[vnA][0];
        p++;
        newNormals[p]= normals[vnA][1];
        p++;
        newNormals[p]= normals[vnA][2];
        p++;
        newNormals[p]= normals[vnB][0];
        p++;
        newNormals[p]= normals[vnB][1];
        p++;
        newNormals[p]= normals[vnB][2];
        p++;
        newNormals[p]= normals[vnC][0];
        p++;
        newNormals[p]= normals[vnC][1];
        p++;
        newNormals[p]= normals[vnC][2];
        p++;
    }
   
}




int main(int argc, const char * argv[])
{
    // Files
    string nameOBJ = argv[1];
    string filepathOBJ = argv[1];
    
    string filepathC = nameOBJ + ".bin";
    

    // insert code here...
    // Model Info
    Model model = getOBJinfo(filepathOBJ);
  
    
    // Model Data
    float positions[model.positions][3];    // XYZ
    float texels[model.texels][2];          // UV
    float normals[model.normals][3];        // XYZ
    int faces[model.faces][9];              // PTN PTN PTN
    
    
    float newposition [model.vertices*3];
    float newUv[model.vertices*2];          // UV
    float newNormals[model.vertices*3];        // XYZ
    
  
    
  
    
    extractOBJdata(filepathOBJ, positions, texels, normals, faces);
   
    rewritePositions(model, faces, positions, newposition);
    reWriteUV(model, faces, texels,newUv);
    reWriteNormals(model, faces, normals,newNormals);
    
    
    
    int fileheader[4]{model.vertices, 16, ((int)sizeof(newposition)+16),(((int)sizeof(newposition)+16)+(int)sizeof(newUv))};

   
    
    ofstream os;
    os.open(filepathC, ios::app|ios::binary|ios::out );
    os.write((char*)&fileheader, sizeof(fileheader));
    os.write((char*)&newposition, sizeof(newposition));
    os.write((char*)&newUv, sizeof(newUv));
    os.write((char*)&newNormals, sizeof(newNormals));
    
    os.close();
    
     
    
       
    return 0;
}



