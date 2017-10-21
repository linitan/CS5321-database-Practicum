package Interpreter;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import Tuple.Tuple;
import net.sf.jsqlparser.schema.Column;

/**
 * @author benzhang tang
 * 
 * BinaryTR class offers an efficient way to read tuples from a binary file of tuples 
 * File format: input file is a sequence of pages of (4*1024) bytes. # of attributes and # of tuples on the page
 * are two metadata stored in each page. Each field of the tuple is stored as 4 bytes integer. 
 * Feature: Java NIO is used in this class to increase the speed of I/O
 */
public class BinaryTR implements TupleReader {

	int Num_Attributes =0;            // number of attributes in a tuple
	int TupleNum_on_page =0;		  // number of tuples in a page
	private static final int buffer_size = 4*1024; // size of the buffer page
	private ByteBuffer buffer;     //buffer page
	private File file;			//file name
	
	private FileChannel fc;
	private int index = 0;
	private FileInputStream fin;
    private Queue<String> records = new LinkedList<>();
   
/*
 * constructor of the class with input parameter of file of table
 */

//	public BinaryTR(File file) throws IOException  {
//		File input = file;
//		//1. read the file into steam	
//		try {
//			FileInputStream fis = new FileInputStream(input);
//
//			//2. allocate a channel to read file
//			FileChannel channel = fis.getChannel();
//
//			//3. allocate a buffer to read the file in the fixed-size chunks, and initialize it
//			buffer = ByteBuffer.allocate(buffer_size);
//			buffer.clear();
////			buffer.putInt( 0, Num_Attributes);
////			buffer.putInt( 5, TupleNum_on_page);
//
////			int remind = (1024-2) % Num_Attributes;
////			int total_tuples = (1024-2-remind)/Num_Attributes;
////			int current_tuple = 0;
//
//			//initialize a array for tuples to be stored into buffer in a bunch
////			int[] tupleArr = new int [(int)channel.size()/4];
//			int[] tupleArr = new int [Num_Attributes];
//
//			//len is the number of bytes read
//			long len = 0;
//			//The offset within the array of the first byte to be written
//			int offset =  8;
//
//			//Reads a sequence of bytes from this channel into the given buffer until the channel is empty 
//			while ((len = channel.read(buffer))!= -1) {
//				//convert the buffer from writing data to buffer from disk to reading mode
//				buffer.flip();
//
//				//transfers bytes from this buffer into the given destination array. 
//				//If there are fewer bytes remaining in the buffer than are required to satisfy the request, 
//				//that is, if length > remaining(), then no bytes are transferred and a BufferUnderflowException is thrown.
////				buffer.asIntBuffer().get(tupleArr,offset,(int)len/4);
////				buffer.asIntBuffer().get(tupleArr,offset,Num_Attributes);
//				buffer.getInt(offset);
//				//next position in buffer to start
//				offset += (int) 4*Num_Attributes;
//				buffer.clear();
//			}
//
//			//4.  read a page of raw bytes, up to 6k bytes to buffer till -1 meaning eof.
//
//			channel.read( buffer );
//
//			//5. 
//
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

    /**constructor*/
    public BinaryTR(File file) throws IOException  {
    	File input = file;
    	try {
            fin = new FileInputStream(input);
            fc = fin.getChannel();
            buffer = ByteBuffer.allocate(buffer_size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 



	public String ReadNextTuple() {
		// TODO Auto-generated method stub
		 String record = "";
	        if (records.isEmpty()) {
	            try {
	                buffer.clear();
	                int r = fc.read(buffer);
	                if (r == -1) {
	                    return null;
	                }
	                index = 0;
	                Num_Attributes = buffer.getInt(index);
	                TupleNum_on_page = buffer.getInt(index + 4);
	                index += 8;
	                for (int i = 0; i < TupleNum_on_page; i++) {
	                    for (int j = 0; j < Num_Attributes; j++) {
	                        int value = buffer.getInt(index);
	                        record += Integer.toString(value) + ",";
	                        index += 4;
	                    }
	                    records.add(record);
	                    record = "";
	                }
	                index = 8;
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        record = records.poll();
	        index += 4;
	        record = record.substring(0, record.length() - 1);
		return record;
	}

//	@Override
//	public void close() {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		try {
            fin.close();
            FileInputStream fin = new FileInputStream(file);
            fc = fin.getChannel();
            buffer = ByteBuffer.allocate(buffer_size);
        } catch (IOException e) {
            e.printStackTrace();
        }

	}



	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
	}
}