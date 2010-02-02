/**
 * Copyright (C) 2009-2010 Wilfred Springer
 *
 * This file is part of Preon.
 *
 * Preon is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * Preon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Preon; see the file COPYING. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */
package nl.flotsam.preon.binding;

import nl.flotsam.limbo.Expression;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import nl.flotsam.preon.Builder;
import nl.flotsam.preon.Codec;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.channel.BitChannel;
import nl.flotsam.preon.buffer.BitBuffer;

import java.io.IOException;

/**
 * The interface of objects that are able to load object state from a
 * {@link BitBuffer} and store object state into a {@link BitBuffer}. Note the
 * difference with a {@link Codec}. A {@link Codec} is capable of creating
 * <em>new</em> objects. {@link Binding Bindings} are expected to unload their
 * data into an existing object.
 * 
 * <p>
 * The {@link Binding} abstraction is key to the inner workings of the
 * {@link nl.flotsam.preon.codec.ObjectCodecFactory}. The reason why it is a public interface instead
 * of an internal one is to allow you to plugin in other kinds of Binding. The
 * typical example here is the {@link ConditionalBindingFactory}. This
 * {@link BindingFactory} creates {@link Binding Binding} instances that respect
 * conditions set as annotations on {@link Fields}.
 * </p>
 * 
 * @author Wilfred Springer
 * 
 */
public interface Binding {

    /**
     * Loads a value from the {@link BitBuffer} and uses the value to populate a
     * field on the object.
     * 
     * @param object
     *            The Object on which fields need to be populated.
     * @param buffer
     *            The buffer from which data will be taken.
     * @param resolver
     *            The object capable of returning values for references passed
     *            in.
     * @param builder
     *            The builder that will be used when - while loading data from
     *            the {@link BitBuffer} - the Binding is (indirectly) required
     *            to create a default instance of a type.
     * @throws DecodingException
     *             If we fail to decode the fields value from the
     *             {@link BitBuffer}.
     */
    void load(Object object, BitBuffer buffer, Resolver resolver,
            Builder builder) throws DecodingException;

    /**
     * Describes this {@link Binding} in the paragraph passed in.
     * 
     * @param <T>
     *            The type of the container for this paragraph.
     * @param <V>
     *            The paragraph in which the content has been written.
     * @param contents
     *            The paragraph in which content will be written.
     * @param resolver
     *            The object capable of rendering references in a human-readable
     *            way.
     * @return The same object as passed in.
     */
    <V extends SimpleContents<?>> V describe(V contents);

    /**
     * Writes a (potentially hyperlinked) reference in the paragraph passed in.
     */
    <T, V extends ParaContents<T>> V writeReference(V contents);

    /**
     * Returns an array of types that could potentially be instantiated while
     * decoding the field's value.
     * 
     * @return An array of types that could potentially be instantiated while
     *         decoding the field's value.
     */
    Class<?>[] getTypes();

    /**
     * Returns the value by applying the binding to a certain context.
     * 
     * @param context
     *            The object to bind to.
     * @return The value.
     */
    Object get(Object context) throws IllegalArgumentException,
            IllegalAccessException;

    /**
     * Returns the name of the binding.
     * 
     * @return The name of the binding.
     */
    String getName();

    /**
     * Returns an {@link Expression} indicating the amount of bits required for
     * representing the value handled by this binding.
     * 
     * @return An {@link Expression} evaluating to the amount of bits required
     *         for representing the value handled by this binding.
     */
    Expression<Integer, Resolver> getSize();

    /**
     * Returns a unique identifier for this binding. (Guaranteed to be unique
     * for the Codec created.) Note that decorators are expected to leave
     * this id untouched.
     * 
     * @return A unique identifier for this binding.
     */
    String getId();

    /**
     * Returns the type of object expected to be loaded by this binding.
     * 
     * @return The type of object expected to be loaded by this binding.
     */
    Class<?> getType();

    /**
     * Saves the state of the bound value to the {@link nl.flotsam.preon.channel.BitChannel} passed in.
     *
     * @param value The value to be stored.
     * @param channel The channel receiving the encoded representation.
     * @param resolver The resolver, used to resolve variable references.
     */
    void save(Object value, BitChannel channel, Resolver resolver) throws IOException;
}