package org.apache.tinkerpop.gremlin.orientdb.structure;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.NotImplementedException;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.Io;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ODocumentInternal;


public final class OrientGraph implements Graph {

    public static final Logger LOGGER = LoggerFactory.getLogger(OrientGraph.class);
    protected ODatabaseDocumentTx database;

    public OrientGraph(ODatabaseDocumentTx iDatabase) {
        this.database = iDatabase;
    }

    @Override
    public Vertex addVertex(Object... keyValues) {
        throw new NotImplementedException();
    }

    @Override
    public <C extends GraphComputer> C compute(Class<C> graphComputerClass) throws IllegalArgumentException {
        throw new NotImplementedException();
    }

    @Override
    public GraphComputer compute() throws IllegalArgumentException {
        throw new NotImplementedException();
    }

    @Override
    public Iterator<Vertex> vertices(Object... vertexIds) {
        //TODO: limit on vertexIds
        boolean polymorphic = true;
        // String iClassName = OrientVertexType.CLASS_NAME;
        String elementClass = "V";
        OrientGraph graph = this;

        // return new OrientElementScanIterable<Vertex>(this, iClassName, iPolymorphic);
        return new Iterator<Vertex>() {

            private final Iterator<ORecord> itty =
                new ORecordIteratorClass<ORecord>(database, database, elementClass, polymorphic);

            @Override
            public boolean hasNext() {
                return itty.hasNext();
            }

            @Override
            public Vertex next() {
                OrientElement currentElement = null;

              if (!hasNext()) throw new NoSuchElementException();
              Object current = itty.next();
              if (current == null) throw new NoSuchElementException();

              if (current instanceof OIdentifiable)
                current = ((OIdentifiable) current).getRecord();

              if (current instanceof ODocument) {
                final ODocument currentDocument = (ODocument) current;

                if (currentDocument.getInternalStatus() == ODocument.STATUS.NOT_LOADED)
                  currentDocument.load();

                if (ODocumentInternal.getImmutableSchemaClass(currentDocument) == null)
                  throw new IllegalArgumentException(
                      "Cannot determine the graph element type because the document class is null. Probably this is a projection, use the EXPAND() function");

                // if (ODocumentInternal.getImmutableSchemaClass(currentDocument).isSubClassOf(graph.getEdgeBaseType()))
                //   currentElement = new OrientEdge(graph, currentDocument);
                // else
                  currentElement = new OrientVertex(graph, currentDocument);
              }

              return (Vertex) currentElement;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Iterator<Edge> edges(Object... edgeIds) {
        throw new NotImplementedException();
    }

    @Override
    public Transaction tx() {
        throw new NotImplementedException();
    }

    @Override
    public Variables variables() {
        throw new NotImplementedException();
    }

    @Override
    public Configuration configuration() {
        throw new NotImplementedException();
    }

    @Override
    public void close() throws Exception {
    }
}
