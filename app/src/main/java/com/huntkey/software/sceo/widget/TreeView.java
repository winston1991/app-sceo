package com.huntkey.software.sceo.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.huntkey.software.sceo.bean.TreeElement;
import com.huntkey.software.sceo.ui.adapter.TreeViewAdapter;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
/**
 * 联系人tree
 * @author chenliang3
 *
 */
public class TreeView extends ListView implements OnItemClickListener{

    private static String TAG = "TreeView";
    
    List<TreeElement> treeElements = null;// 所有节点集合
    List<TreeElement> currentElements = null;// 当前显示的节点集合
    List<TreeElement> tempElements = null;// 用于临时存储
     
    HashMap<String, List<TreeElement>> deleteMap = new HashMap<String, List<TreeElement>>();
    //用于存储要删除的子节点，以及间接子节点。
    TreeViewAdapter adapter = null;// 用于数据填充
    LastLevelItemClickListener itemClickCallBack;// 用户点击事件回调
    ParentLevelItemClickListener pItemClickCallBack;//用户点击父节点事件回调----------------cl
  
    public TreeView(final Context context, AttributeSet attrs) {
       super(context, attrs);
       Log.d(TAG, "create with TreeView(Context context, AttributeSet attrs)");
       treeElements = new ArrayList<TreeElement>();
       currentElements = new ArrayList<TreeElement>();
       
       adapter = new TreeViewAdapter(context, currentElements);
       this.setAdapter(adapter);
       itemClickCallBack = new LastLevelItemClickListener() {
           @Override
           public void onLastLevelItemClick(int position,TreeViewAdapter adapter) {
              Log.d(TAG, "last level element "
                     + currentElements.get(position).getTitle()
                     + " is clicked");
           }
       };
       
       //-----------------------cl
       pItemClickCallBack = new ParentLevelItemClickListener() {
		
    	   @Override
    	   public void onParentItemClick(int position, TreeViewAdapter adapter) {
    		   
    	   }
       };
       
       this.setOnItemClickListener(this);
    }
    
    public void initData(Context context, List<TreeElement> treeElements) {
       this.treeElements = treeElements;
       getFirstLevelElements(context);
       adapter.notifyDataSetChanged();
    }
    
    /**
     * 更新父节点下的数据---------------------------cl
     */
    public void updateData(Context context, List<TreeElement> treeElements, int position, String empCode){
    	this.treeElements = treeElements;
    	
    	currentElements.addAll(position + 1,this.getChildElementsFromAllById(empCode));
    	
    	adapter.notifyDataSetChanged();
    }
  
    /**
     * 设置点击事件回调接口
     *
     * @param itemClickCallBack
     */
    public void setLastLevelItemClickCallBack(LastLevelItemClickListener itemClickCallBack) {
       this.itemClickCallBack = itemClickCallBack;
    }
    
    /**
     * 设置父节点点击事件回调接口------------------------cl
     */
    public void setParentLevelItemClickCallBack(ParentLevelItemClickListener pItemClickCallBack){
    	this.pItemClickCallBack = pItemClickCallBack;
    }
  
    /**
     * 初始化树形结构列表数据,把第一层级的数据添加到currentElements中
     */
    public void getFirstLevelElements(Context context) {
       Log.d(TAG, "initCurrentElements");
       int size = treeElements.size();
       Log.d(TAG, "tree elements num is: " + size);
       if (currentElements == null) {
           currentElements = new ArrayList<TreeElement>();
       }
       currentElements.clear();
       for (int i = 0; i < size; i++) {
    	   if (treeElements.get(i).getLevel() == 1) {
              currentElements.add(treeElements.get(i));
              Log.d(TAG, "find a first level element: " + treeElements.get(i));
  
           }
       }
    }
  
    /**
     * 从所有节点集合中获取某父节点的子节点集合
     *
     * @param parentId
     * @return
     */
    private List<TreeElement> getChildElementsFromAllById(String parentId) {
       tempElements = new ArrayList<TreeElement>();
       int size = treeElements.size();
  
       for (int i = 0; i < size; i++) {
           String temptParentId = treeElements.get(i).getParentId() ;
           if(temptParentId != null){
               if (temptParentId.equalsIgnoreCase(parentId)) {
                      tempElements.add(treeElements.get(i));
                      Log.d(TAG, "find a child element： " + treeElements.get(i));
               }
           }
       }
       return tempElements;
    }
  
    /**
     * 从当前显示的节点集合中    , 获取某父节点的子节点集合
     * @param parentId
     * @return
     */
    private List<TreeElement> getChildElementsFromCurrentById(String parentId) {
       Log.d(TAG, "getChildElementsFromCurrentById    parentId： " + parentId);
       if (tempElements == null) {
           tempElements = new ArrayList<TreeElement>();
       } else {
           tempElements.clear();
       }
  
       int size = currentElements.size();
       if(size > 0){
           for(TreeElement treeElement : currentElements){
               String temp = treeElement.getParentId();
               if(temp != null){
                   if (temp.equalsIgnoreCase(parentId)) {
                       tempElements.add(treeElement);
                       Log.d(TAG, "find a child element to delete： " +treeElement);
                    }
 
               }
           }
       }
       return tempElements;
    }
  
    /**
     * 删除某父节点的所有子节点集合(包括直接节点和间接节点)
     * @param parentId
     * @return
     */
    private synchronized boolean delAllChildElementsByParentId(String parentId) {
       Log.e(TAG, "delAllChildElementsByParentId: " + parentId);
        
       List<TreeElement> childElments = getChildElementsFromCurrentById(parentId);
       List<TreeElement> treeElementsToDel = deleteMap.get(parentId);
       if (treeElementsToDel == null) {
           treeElementsToDel = new ArrayList<TreeElement>();
           deleteMap.put(parentId, treeElementsToDel);
       } else {
           treeElementsToDel.clear();
       }
       int size = childElments.size();
       Log.e(TAG, "childElments size : " + size);
       TreeElement childElement = null;
       for (int i = 0; i < size; i++) {
           childElement = childElments.get(i);
           //寻找间接节点
           if (childElement.isHasChild() && childElement.isFold()) {
              treeElementsToDel.add(childElement);
           }
       }
       size = treeElementsToDel.size();
       Log.e(TAG, "treeElementsToDel size : " + size);
  
       if(size > 0){
           //删除间接节点
           List<TreeElement> deleteTreeElement = treeElementsToDel;
           for(TreeElement element : deleteTreeElement){
               delAllChildElementsByParentId(element.getCc_code());
           }
       }
       //删除直接节点
       delDirectChildElementsByParentId(parentId);
       return true;
    }
  
    /**
     * 删除某父节点的直接子节点集合
     *
     * @param parentId
     * @return
     */
    private synchronized boolean delDirectChildElementsByParentId(
           String parentId) {
       Log.d(TAG, "delDirectChildElementsByParentId(): " + parentId);
       boolean success = false;
       if (currentElements == null || currentElements.size() == 0) {
           Log.d(TAG, "delChildElementsById() failed,currentElements is null or it's size is 0");
           return success;
       }
       synchronized (currentElements) {
           int size = currentElements.size();
           Log.d(TAG, "begin delete");
           for (int i = size - 1; i >= 0; i--) {
              String temp = currentElements.get(i).getParentId(); 
              if(temp != null && temp.equalsIgnoreCase(parentId)){
                  currentElements.get(i).setFold(false);// 记得隐藏子节点时把展开状态设为false
                  currentElements.remove(i);
              }
           }
        }
       success = true;
       return success;
    }
  
    /**
     * 根据id查下标
     *
     * @param id
     * @return
     */
    @SuppressWarnings("unused")
    private int getElementIndexById(String id) {
       int num = currentElements.size();
       for (int i = 0; i < num; i++) {
           if (currentElements.get(i).getCc_code().equalsIgnoreCase(id)) {
              return i;
           }
       }
       return -1;
    }
     
    @SuppressWarnings("unused")
    private TreeElement getElementById(String id){
         int num = currentElements.size();
         if( num > 0){
             for (int i = 0; i < num; i++) {
                 if (currentElements.get(i).getCc_code().equalsIgnoreCase(id)) {
                    return currentElements.get(i);
                 }
             }
         }
         return null;
    }
  
    @Override
    public void onItemClick(AdapterView<?> arg0, View convertView,
           int position, long id) {
       TreeElement element = currentElements.get(position);
       if (element.isHasChild()) {// 当前节点有子节点时只进行数据显示或隐藏等操作
           if (!element.isFold()) {// 当前父节点为未展开状态
        	  //加载父节点下的数据------------------cl
        	  pItemClickCallBack.onParentItemClick(position, adapter);
        	  
              currentElements.addAll(position + 1,this.getChildElementsFromAllById(element.getCc_code()));
           } else if (element.isFold()) {// 当前父节点为展开状态
              boolean success = this.delAllChildElementsByParentId(element.getCc_code());
              Log.d(TAG, "delete child state: " + success);
              if (!success) {
                  return;
              }
           }
           element.setFold(!element.isFold());// 设置反状态
           adapter.notifyDataSetChanged();// 刷新数据显示
       } else {// 当前节点没有子节点时只进行用户自定义操作
           itemClickCallBack.onLastLevelItemClick(position,adapter);
       }
  
    }
    
    /**
     * 自定义内部接口，用于用户点击父节点时加载父节点下的数据---------------cl
     */
    public interface ParentLevelItemClickListener{
    	public void onParentItemClick(int position, TreeViewAdapter adapter);
    }
  
    /**
     * 自定义内部接口，用于用户点击最终节点时的事件回调
     */
    public interface LastLevelItemClickListener {
       public void onLastLevelItemClick(int position, TreeViewAdapter adapter);
    }

}
