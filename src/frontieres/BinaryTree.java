package frontieres;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class BinaryTree<E extends Comparable> implements Collection<E> {

	private class Tree<E>{
		public E val;
		public Tree<E> g;
		public Tree<E> d;
		public Tree(E val){
			this.val = val;
		}
	}
	
	private Tree<E> tree;
	private int size;
	public BinaryTree(){
		tree = null;
		size = 0;
	}
	
	@Override
	public boolean add(E arg) {
		if(tree == null){
			tree = new Tree(arg);
			size++;
			return true;
		}
		
		Tree<E> ref = tree;
		
		while(true){
			if(arg.compareTo(ref.val) >= 0){
				if(ref.d == null){
					ref.d = new Tree<E>(arg);
					break;
				}
				else{
					ref = ref.d;
				}
			}
			else{
				if(ref.g == null){
					ref.g = new Tree<E>(arg);
					break;
				}
				else{
					ref = ref.g;
				}
			}
		}
		
		size++;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		for(E val : arg0){
			this.add(val);
		}
		return true;
	}

	@Override
	public void clear() {
		tree = null;
		
	}

	@Override
	public boolean contains(Object arg0) {
		for(E val : this){
			if(arg0.equals(val)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		for(Object val : arg0){
			if(!this.contains(val)){
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return tree == null;
	}

	@Override
	public Iterator<E> iterator() {
		LinkedList<E> l = new LinkedList<E>();
		
		iterate(l, tree);
		
		return l.iterator();
	}

	@Override
	public boolean remove(Object arg0) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		return false;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return null;
	}

	private void iterate(LinkedList<E> l, Tree<E> tree){
		if(tree != null){
			iterate(l, tree.g);
			l.add(tree.val);
			iterate(l, tree.d);
		}
	}
}
