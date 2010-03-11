package org.jquantlib.math.matrixutilities.internal;

import java.util.Set;

import org.jquantlib.lang.exceptions.LibraryException;


public abstract class DirectAddress implements Address {

    protected final int row0;
    protected final int row1;
    protected final Address chain;
    protected final int col0;
    protected final int col1;
    protected final Set<Address.Flags> flags;
    protected final int rows;
    protected final int cols;

    protected final int base;
    protected final int last;


    //
    // public methods
    //

    public DirectAddress(
                final int row0, final int row1,
                final Address chain,
                final int col0, final int col1,
                final Set<Address.Flags> flags,
                final int rows, final int cols) {
        this.chain = chain;
        this.flags = flags;

        final int offset = ( fortran() ? 1 : 0 ) - ( (chain==null) ? 0 : ( chain.fortran() ? 1 : 0 ) );
        this.row0 = offset + ( chain==null ? row0 : row0 + chain.row0() );
        this.col0 = offset + ( chain==null ? col0 : col0 + chain.col0() );
        this.row1 = this.row0 + (row1-row0);
        this.col1 = this.col0 + (col1-col0);
        this.rows = chain==null ? rows : chain.rows();
        this.cols = chain==null ? cols : chain.cols();

        this.base = (row0-offset)*rows + (col0-offset);
        this.last = (row1-offset)*cols + (col1-offset+1);
    }

    @Override
    public boolean contiguous() {
        return flags.contains(Address.Flags.CONTIGUOUS);
    }

    @Override
    public boolean fortran() {
        return flags.contains(Address.Flags.FORTRAN);
    }

    @Override
    public Set<Address.Flags> flags() {
        return flags;
    }

    @Override
    public int rows() {
        return rows;
    }

    @Override
    public int cols() {
        return cols;
    }

    @Override
    public int row0() {
        return row0;
    }

    @Override
    public int col0() {
        return col0;
    }

    @Override
    public int base() {
        return base;
    }

    @Override
    public int last() {
        if (!contiguous())
            throw new LibraryException(GAP_INDEX_FOUND);
        return last;
    }


    //
    // protected inner classes
    //

    protected abstract class FastAddressOffset implements Address.Offset {

        protected int row;
        protected int col;

        protected FastAddressOffset() {
            // only protected access allowed
        }

    }


}
