package com.example.mediaconverter.data;

import android.database.Cursor;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class HistoryDao_Impl implements HistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<HistoryItem> __insertionAdapterOfHistoryItem;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public HistoryDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfHistoryItem = new EntityInsertionAdapter<HistoryItem>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `history` (`id`,`input_url`,`output_file_path`,`output_format`,`created_at`,`status`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, HistoryItem value) {
        stmt.bindLong(1, value.getId());
        if (value.getInputUrl() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getInputUrl());
        }
        if (value.getOutputFilePath() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getOutputFilePath());
        }
        if (value.getOutputFormat() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getOutputFormat());
        }
        final Long _tmp = __converters.dateToTimestamp(value.getCreatedAt());
        if (_tmp == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindLong(5, _tmp);
        }
        if (value.getStatus() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getStatus());
        }
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM history WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM history";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final HistoryItem item, final Continuation<? super Long> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          long _result = __insertionAdapterOfHistoryItem.insertAndReturnId(item);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public List<HistoryItem> getAll() {
    final String _sql = "SELECT * FROM history ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfInputUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "input_url");
      final int _cursorIndexOfOutputFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "output_file_path");
      final int _cursorIndexOfOutputFormat = CursorUtil.getColumnIndexOrThrow(_cursor, "output_format");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final List<HistoryItem> _result = new ArrayList<HistoryItem>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final HistoryItem _item;
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        final String _tmpInputUrl;
        if (_cursor.isNull(_cursorIndexOfInputUrl)) {
          _tmpInputUrl = null;
        } else {
          _tmpInputUrl = _cursor.getString(_cursorIndexOfInputUrl);
        }
        final String _tmpOutputFilePath;
        if (_cursor.isNull(_cursorIndexOfOutputFilePath)) {
          _tmpOutputFilePath = null;
        } else {
          _tmpOutputFilePath = _cursor.getString(_cursorIndexOfOutputFilePath);
        }
        final String _tmpOutputFormat;
        if (_cursor.isNull(_cursorIndexOfOutputFormat)) {
          _tmpOutputFormat = null;
        } else {
          _tmpOutputFormat = _cursor.getString(_cursorIndexOfOutputFormat);
        }
        final Date _tmpCreatedAt;
        final Long _tmp;
        if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getLong(_cursorIndexOfCreatedAt);
        }
        final Date _tmp_1 = __converters.fromTimestamp(_tmp);
        if(_tmp_1 == null) {
          throw new IllegalStateException("Expected non-null java.util.Date, but it was null.");
        } else {
          _tmpCreatedAt = _tmp_1;
        }
        final String _tmpStatus;
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _tmpStatus = null;
        } else {
          _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
        }
        _item = new HistoryItem(_tmpId,_tmpInputUrl,_tmpOutputFilePath,_tmpOutputFormat,_tmpCreatedAt,_tmpStatus);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Flow<List<HistoryItem>> getAllFlow() {
    final String _sql = "SELECT * FROM history ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"history"}, new Callable<List<HistoryItem>>() {
      @Override
      public List<HistoryItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfInputUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "input_url");
          final int _cursorIndexOfOutputFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "output_file_path");
          final int _cursorIndexOfOutputFormat = CursorUtil.getColumnIndexOrThrow(_cursor, "output_format");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<HistoryItem> _result = new ArrayList<HistoryItem>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final HistoryItem _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpInputUrl;
            if (_cursor.isNull(_cursorIndexOfInputUrl)) {
              _tmpInputUrl = null;
            } else {
              _tmpInputUrl = _cursor.getString(_cursorIndexOfInputUrl);
            }
            final String _tmpOutputFilePath;
            if (_cursor.isNull(_cursorIndexOfOutputFilePath)) {
              _tmpOutputFilePath = null;
            } else {
              _tmpOutputFilePath = _cursor.getString(_cursorIndexOfOutputFilePath);
            }
            final String _tmpOutputFormat;
            if (_cursor.isNull(_cursorIndexOfOutputFormat)) {
              _tmpOutputFormat = null;
            } else {
              _tmpOutputFormat = _cursor.getString(_cursorIndexOfOutputFormat);
            }
            final Date _tmpCreatedAt;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            final Date _tmp_1 = __converters.fromTimestamp(_tmp);
            if(_tmp_1 == null) {
              throw new IllegalStateException("Expected non-null java.util.Date, but it was null.");
            } else {
              _tmpCreatedAt = _tmp_1;
            }
            final String _tmpStatus;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmpStatus = null;
            } else {
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            }
            _item = new HistoryItem(_tmpId,_tmpInputUrl,_tmpOutputFilePath,_tmpOutputFormat,_tmpCreatedAt,_tmpStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
