/*
 * Copyright (C) 2012 THM webMedia
 *
 * This file is part of ARSnova.
 *
 * ARSnova is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ARSnova is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.thm.arsnova.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import de.thm.arsnova.dao.IDatabaseDao;
import de.thm.arsnova.entities.FoodVote;

@Service
public class FoodService implements IFoodService {

	@Autowired
	private IDatabaseDao databaseDao;

	@Autowired
	private IUserService userService;

	public final void setDatabaseDao(IDatabaseDao databaseDao) {
		this.databaseDao = databaseDao;
	}

	@Override
	@PreAuthorize("isAuthenticated()")
	public void vote(String menu) {
		this.databaseDao.vote(userService.getCurrentUser(), menu);

	}

	@Override
	public List<FoodVote> getFoodVote() {
		return this.databaseDao.getFoodVote();
	}

	@Override
	public int getFoodVoteCount() {
		return this.databaseDao.getFoodVoteCount();
	}
}
