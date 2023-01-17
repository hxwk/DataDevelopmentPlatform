package com.dfssi.hbase.v2.search;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ResultEntry<T> {

		private Collection<T> resut;
        private int rowCount;

		ResultEntry(Collection<T> resut, int rowCount){
			this.resut = resut;
			this.rowCount = rowCount;
		}

		public Collection<T> getResut() {
			return resut;
		}

		public int getRowCount() {
			return rowCount;
		}

		public List<T> getResultAsList(){
			return (List<T>) resut;
		}

		public Set<T> getResultAsSet(){
			return (Set<T>) resut;
		}

		void addResut(ResultEntry<T> resultEntry){
			this.resut.addAll(resultEntry.getResut());
			this.rowCount += resultEntry.getRowCount();
		}

		@Override
		public String toString() {
			return "ResultEntry{" +
					"resut=" + resut +
					", rowCount=" + rowCount +
					'}';
		}
	}